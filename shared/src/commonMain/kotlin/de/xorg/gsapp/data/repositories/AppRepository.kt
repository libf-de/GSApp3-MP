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
import de.xorg.gsapp.data.exceptions.NoEntriesException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sources.defaults.DefaultsDataSource
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

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

    companion object {
        val log = logging()
    }

    //Get data sources from DI
    private val apiDataSource: RemoteDataSource by inject()
    private val localDataSource: LocalDataSource by inject()
    private val defaultsDataSource: DefaultsDataSource by inject()

    override fun getSubstitutions(): Flow<Result<SubstitutionSet>>
        = localDataSource.getSubstitutionPlanFlow()

    /*override fun getFilteredSubstitutions(): Flow<Result<SubstitutionSet>> = combine(
        getSubstitutions(), getRoleFlow(), getFilterValueFlow()
    ) { subs, role, filter ->
        if(role == FilterRole.ALL) return@combine subs

        subs.mapCatching {
            it.copy(
                substitutions = when(role) {
                    FilterRole.STUDENT -> {
                        it.substitutions.filterKeys { entry ->
                            entry.lowercase().contains(filter.lowercase())
                        }
                    }

                    FilterRole.TEACHER -> {
                        it.substitutions.mapValues { subsPerKlass ->
                            subsPerKlass.value.filter { aSub ->
                                aSub.substTeacher.shortName.lowercase() == filter.lowercase()
                            }
                        }.filter { subsPerKlass ->
                            subsPerKlass.value.isNotEmpty()
                        }
                    }

                    else -> { //TODO: Is returning instantly much faster than this?
                        it.substitutions
                    }
                }
            )
        }

    }*/

    override suspend fun updateSubstitutions(callback: (Result<Boolean>) -> Unit) {
        log.d { "updateSubstitutions in AppRepository" }
        try {
            log.d { "updateSubstitutions in try" }
            with(apiDataSource.getSubstitutionPlan()) {
                log.d { "updateSubstitutions in with" }
                if(this.isFailure) {
                    log.w { "getSubstitutionPlan failed :/" }
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (api) :(")
                        )
                    )
                    return
                }

                /*val apiSds = this.getOrNull()!!

                val dbCandidateId = localDataSource.findIdByDateString(apiSds.dateStr)
                if(dbCandidateId.isFailure) {
                    callback(
                        Result.failure(
                            dbCandidateId.exceptionOrNull() ?: Exception("unknown cause (candidate) :(")
                        )
                    )
                }*/



                //TODO: Compare remote vs. local by hash AND DATE? (https://github.com/libf-de/GSApp3-MP/issues/8)
                val localLatestHash = localDataSource.getLatestSubstitutionHash()
                if(localLatestHash.isFailure) {
                    log.w { "getLocalHash failed :/" }
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (dbLatest) :(")
                        )
                    )
                    return
                }

                //If remote plan is different from latest local -> store it!
                if(localLatestHash.getOrNull()!! != this.getOrNull()!!.hashCode()) {
                    log.d { "got new plan!" }
                    localDataSource.addSubstitutionPlan(this.getOrNull()!!)
                    localDataSource.cleanupSubstitutionPlan()
                    callback(Result.success(true))
                } else {
                    log.d { "no new plan!" }
                    localDataSource.cleanupSubstitutionPlan()
                    callback(Result.success(false))
                }
            }
        } catch (ex: Exception) {
            log.w { ex.stackTraceToString() }
            callback(Result.failure(ex))
        }
    }

    override fun getFoodplan(): Flow<Result<Map<LocalDate, List<Food>>>>
    = combine(localDataSource.getFoodMapFlow(), localDataSource.getAdditivesFlow()) { food, ad ->
        if(food.isFailure || ad.isFailure) return@combine food

        val adMap = ad.getOrNull()!!
        food.mapCatching { outerMap ->
            outerMap.mapValues { foodMap ->
                foodMap.value.map { foodEntry ->
                    foodEntry.copy(
                        additives = foodEntry.additives.map { adShort -> adMap[adShort] ?: adShort }
                    )
                }
            }
        }
    }

    override suspend fun updateFoodplan(callback: (Result<Boolean>) -> Unit) {
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

                if(this.getOrNull()!!.first.isEmpty()) {
                    callback(
                        Result.failure(
                            NoEntriesException()
                        )
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

    override fun getExams(): Flow<Result<List<Exam>>> = localDataSource.getExamsFlow()

    override suspend fun updateExams(callback: (Result<Boolean>) -> Unit) {
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
                    localDataSource.clearAndAddAllExams(this.getOrNull()!!)
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

    override suspend fun updateTeachers(callback: (Result<Boolean>) -> Unit) {
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

    override suspend fun updateSubjects(force: Boolean, callback: (Result<Boolean>) -> Unit) {
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
            log.w { ex.stackTraceToString() }
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