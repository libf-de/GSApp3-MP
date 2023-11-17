/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.data.repositories

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sources.defaults.DefaultsDataSource
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * This combines all data sources (local cache and remote apis), as well as app settings into a
 * single interface to be used by the UI layer.
 *
 * All of the "get" flows (marked with |GET|) roughly follow this chart:
 * (if flow does not support reload ==>  reloading = false)
 * ┌──────────┐
 * │Reloading?│
 * └┬──────┬──┘
 *  │Yes   │No
 *  │    ┌─▼────────────────┐
 *  │    │Load local cache. │
 *  │    │Was it successful?│
 *  │    └──┬────────┬──────┘
 *  │       │No      │Yes
 *  │       │      ┌─▼──────┐
 *  │       │      │Emit it!│
 *  │       │      └─┬──────┘
 *  │       │        │
 * ┌▼───────▼────────▼────┐
 * │Load from api/website.│
 * │Was it successful?    │
 * └┬──────────────┬──────┘
 *  │No            │Yes
 *  │     ┌────────┴────────┐
 *  │     │Is it different  │
 *  │     │from local cache?│
 *  │     └───┬────────────┬┘
 *  │         │Yes       No│
 *  │  ┌──────▼─────────┐  │
 *  │  │ Emit and write │  │
 *  │  │ to local cache.│  │
 *  │  └─────────────┬──┘  │
 *  │                └─────┤
 *  │                      │
 * ┌▼────────────────────┐ │
 * │Was loading frm local│ │
 * │cache successful AND │ │
 * │are we NOT reloading?│ │
 * ├─ ── ── ── ── ── ── ─┤ │
 * │(Is there any data to│ │
 * │ be displayed?)      │ │
 * └─┬───────────────┬───┘ │
 *   │No          Yes│     │
 *  ┌▼───────┐     ┌─▼─────▼┐
 *  │Emit web│     │ !DONE! │
 *  │error   ├─────► !DONE! │
 *  └────────┘     └────────┘
 */

class AppRepository : GSAppRepository, KoinComponent {
    //Get data sources from DI
    private val apiDataSource: RemoteDataSource by inject()
    private val localDataSource: LocalDataSource by inject()
    private val defaultsDataSource: DefaultsDataSource by inject()

    override suspend fun handleUpdate(dbDefaultsVersion: Int) {
        if(dbDefaultsVersion < 1) {
            updateSubjects(force = true)
        }
    }

    override fun getSubstitutions(): Flow<SubstitutionSet> = localDataSource.getSubstitutionPlanFlow()

    override suspend fun updateSubstitutions(callback: suspend (Result<Boolean>) -> Unit) {
        Napier.d { "updateSubstitutions in AppRepository" }
        try {
            Napier.d { "updateSubstitutions in try" }

            apiDataSource.getSubstitutionPlan()
                .onFailure {
                    callback(Result.failure(it))
                    return
                }
                .onSuccess { subApiModelSet ->
                    //TODO: Compare remote vs. local by hash AND DATE? (https://github.com/libf-de/GSApp3-MP/issues/8)
                    localDataSource.getLatestSubstitutionHashAndDate()
                        .onFailure {
                            Napier.w { "getLocalHash failed :/" }
                            callback(Result.failure(it))
                            return
                        }
                        .onSuccess { localLatestHashAndDate ->
                            if(localLatestHashAndDate.first != subApiModelSet.hashCode()) {
                                Napier.d { "got new plan!" }
                                localDataSource.addSubstitutionPlanAndCleanup(subApiModelSet)
                                callback(Result.success(true))
                            } else {
                                Napier.d { "no new plan!" }
                                localDataSource.cleanupSubstitutionPlan()
                                callback(Result.success(false))
                            }

                            // Update teachers from website if not all
                            localDataSource.getAllTeachersShorts().onSuccess { dbList ->
                                val teachers = dbList.map { it.lowercase() }

                                subApiModelSet.substitutionApiModels
                                    .map {
                                        it.substTeacher.lowercase()
                                    }.filter {
                                        it.isNotBlank() && it.matches(Regex("^[A-Za-z0-9]+\$"))
                                    }.all {
                                        it in teachers
                                    }.ifNot {
                                        updateTeachers()
                                    }
                            }
                        }
                }
        } catch (ex: Exception) {
            Napier.w { ex.stackTraceToString() }
            callback(Result.failure(ex))
        }
    }

    override fun getFoodplan(): Flow<Map<LocalDate, List<Food>>> = combine(localDataSource.getFoodMapFlow(), localDataSource.getAdditivesFlow()) { food, ad ->
        food.mapValues { foodMap ->
            foodMap.value.map { foodEntry ->
                foodEntry.copy(
                    additives = foodEntry.additives.map { adShort -> ad[adShort] ?: adShort }
                )
            }
        }
    }

    override suspend fun updateFoodplan(callback: suspend (Result<Boolean>) -> Unit) {
        try {
            with(apiDataSource.getFoodplanAndAdditives()) {
                if(this.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (api) :(")
                        )
                    )
                    return
                }

                // TODO: Use failure for empty foodplan or success(false)?
                if(this.getOrNull()!!.first.isEmpty()) {
                    callback(
                        Result.success(false)
                    )
                    return
                }

                val localLatestDate = localDataSource.getLatestFoodDate()
                if(localLatestDate.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (dbLatest) :(")
                        )
                    )
                    return
                }

                val remoteLatestDate = this.getOrNull()!!.first.keys.maxOrNull() ?: LocalDate.fromEpochDays(0)

                //If remote plan is newer than latest local -> store it!
                if(localLatestDate.getOrNull()!! < remoteLatestDate) {
                    localDataSource.addFoodMap(this.getOrNull()!!.first)
                    localDataSource.addAllAdditives(this.getOrNull()!!.second)
                    localDataSource.cleanupFoodPlan()
                    callback(Result.success(true))
                } else {
                    localDataSource.cleanupFoodPlan()
                    callback(Result.success(false))
                }
            }
        } catch (ex: Exception) {
            callback(Result.failure(ex))
        }
    }

    override fun getExams(): Flow<List<Exam>> = localDataSource.getExamsFlow()

    override suspend fun updateExams(callback: suspend (Result<Boolean>) -> Unit) {
        try {
            with(apiDataSource.getExams()) {
                if(this.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (api) :(")
                        )
                    )
                    return
                }

                //TODO: Compare remote vs. local by hash AND DATE? (https://github.com/libf-de/GSApp3-MP/issues/8)
                val localLatest = localDataSource.getAllExams()
                if(localLatest.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (dbLatest) :(")
                        )
                    )
                    return
                }

                if(localLatest.getOrNull() != this.getOrNull()!!) {
                    val subjectRegex = Regex("[A-Za-z]+")
                    val examsWithSubjects = this.getOrNull()!!.map { exam ->
                        exam.copy(
                            // Extract the Subject shorts from the label
                            subject = subjectRegex
                                .find(exam.label)
                                ?.value
                                ?.lowercase()
                                ?.capitalize(Locale.current)
                                ?.let {
                                    Subject(it)
                                }
                        )
                    }
                    localDataSource.clearAndAddAllExams(examsWithSubjects)
                    callback(Result.success(true))
                } else {
                    localDataSource.cleanupExams()
                    callback(Result.success(false))
                }
            }
        } catch (ex: Exception) {
            callback(Result.failure(ex))
        }
    }

    override fun getTeachers(): Flow<Result<List<Teacher>>> = localDataSource.getTeachersFlow()

    override suspend fun updateTeachers(callback: suspend (Result<Boolean>) -> Unit) {
        try {
            with(apiDataSource.getTeachers()) {
                if(this.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (api) :(")
                        )
                    )
                    return
                }

                localDataSource.addAllTeachers(this.getOrNull()!!)
            }
        } catch (ex: Exception) {
            callback(Result.failure(ex))
        }
    }

    override suspend fun addTeacher(value: Teacher): Result<Boolean> {
        return try {
            localDataSource.addTeacher(value)
            Result.success(true)
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun editTeacher(oldTea: Teacher, newLongName: String): Result<Teacher> {
        return try {
            localDataSource.updateTeacher(oldTea.copy(longName = newLongName))
            Result.success(oldTea)
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun deleteTeacher(value: Teacher): Result<Boolean> {
        return try {
            localDataSource.deleteTeacher(value)
            Result.success(true)
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }

    override fun getSubjects(): Flow<Result<List<Subject>>> = localDataSource.getSubjectsFlow()

    override suspend fun updateSubjects(force: Boolean, callback: suspend (Result<Boolean>) -> Unit) {
        try {
            with(localDataSource.countSubjects()) {
                if(this.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (countSubjects)")
                            )
                    )
                    return
                }

                if((this.getOrNull() ?: 0) < 1L || force) {
                    localDataSource.addAllSubjects(
                        defaultsDataSource.getDefaultSubjects()
                    )
                    callback(Result.success(true))
                } else {
                    callback(Result.success(false))
                }
            }
        } catch (ex: Exception) {
            callback(Result.failure(ex))
        }
    }

    override suspend fun addSubject(value: Subject): Result<Boolean> {
        return try {
            localDataSource.addSubject(value)
            Result.success(true)
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun deleteSubject(value: Subject): Result<Boolean> {
        return try {
            localDataSource.deleteSubject(value)
            Result.success(true)
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun resetSubjects(): Result<Boolean> {
        return try {
            localDataSource.resetSubjects(
                defaultsDataSource.getDefaultSubjects()
            )
            Result.success(true)
        } catch (ex: Exception) {
            Napier.w { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    override suspend fun editSubject(
        subject: Subject,
        newLongName: String?,
        newColor: Color?
    ): Result<Subject> {
        return try {
            localDataSource.updateSubject(
                Subject(
                    shortName = subject.shortName,
                    longName = newLongName ?: subject.longName,
                    color = newColor ?: subject.color
                )
            )
            Result.success(subject)
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }


    //****************** SETTINGS ******************

}

private suspend fun Boolean.ifNot(func: suspend () -> Unit): Boolean {
    if(!this) func(); return this
}
