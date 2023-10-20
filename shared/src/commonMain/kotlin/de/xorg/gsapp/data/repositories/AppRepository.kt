/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
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
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.coroutines.FlowSettings
import de.xorg.gsapp.data.exceptions.NoEntriesException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sources.defaults.DefaultsDataSource
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import org.kodein.di.DI
import org.kodein.di.instance
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

@OptIn(ExperimentalSettingsApi::class)
class AppRepository(di: DI) : GSAppRepository {

    companion object {
        val log = logging()
    }

    //Get data sources from DI
    private val apiDataSource: RemoteDataSource by di.instance()
    private val localDataSource: LocalDataSource by di.instance()
    private val defaultsDataSource: DefaultsDataSource by di.instance()

    //private val appSettings: Settings by di.instance()
    private val appSettings: FlowSettings by di.instance()

    override fun getSubstitutions(): Flow<Result<SubstitutionSet>>
        = localDataSource.getSubstitutionPlanFlow()

    override fun getFilteredSubstitutions(): Flow<Result<SubstitutionSet>> = combine(
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

    }

    override suspend fun updateSubstitutions(callback: (Result<Boolean>) -> Unit) {
        try {
            with(apiDataSource.getSubstitutionPlan()) {
                if(this.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (api) :(")
                        )
                    )
                    return
                }

                //TODO: Compare remote vs. local by hash AND DATE? (https://github.com/libf-de/GSApp3-MP/issues/8)
                val localLatestHash = localDataSource.getLatestSubstitutionHash()
                if(localLatestHash.isFailure) {
                    callback(
                        Result.failure(
                            this.exceptionOrNull() ?: Exception("unknown cause (dbLatest) :(")
                        )
                    )
                    return
                }

                // If remote plan is older than or the same as the latest local -> don't store it
                // Always clean the database (afterwards).
                /*if(localLatest.getOrNull()!!.first == this.getOrNull()!!.hashCode() ||
                   localLatest.getOrNull()!!.second < this.getOrNull()!!.date) {
                    localDataSource.cleanupSubstitutionPlan()
                    callback(Result.success(false))
                } else {
                    localDataSource.addSubstitutionPlan(this.getOrNull()!!)
                    localDataSource.cleanupSubstitutionPlan()
                    callback(Result.success(true))
                }*/


                //If remote plan is different from latest local -> store it!
                if(localLatestHash.getOrNull()!! != this.getOrNull()!!.hashCode()) {
                    localDataSource.addSubstitutionPlan(this.getOrNull()!!)
                    localDataSource.cleanupSubstitutionPlan()
                    callback(Result.success(true))
                } else {
                    localDataSource.cleanupSubstitutionPlan()
                    callback(Result.success(false))
                }
            }
        } catch (ex: Exception) {
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

                if(localLatest.getOrNull() == this.getOrNull()!!) {
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

    override suspend fun updateSubjects(callback: (Result<Boolean>) -> Unit) {
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

                if((this.getOrNull() ?: 0) < 1L) {
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
    /**
     * Returns the Filter Role (Student/Teacher/All) from settings
     * @return FilterRole
     */
    override suspend fun getRole(): FilterRole {
        return FilterRole.fromInt(
            appSettings.getInt("role", FilterRole.default.value)
        )
    }

    /**
     * Returns a flow for Filter Role (Student/Teacher/All) from settings
     * @return Flow<FilterRole>
     */
    override fun getRoleFlow(): Flow<FilterRole>
        = appSettings.getIntFlow("role", FilterRole.default.value)
                     .map { FilterRole.fromInt(it) }

    /**
     * Stores the Filter Role (Student/Teacher/All) in settings
     * @param value role to store
     */
    override suspend fun setRole(value: FilterRole) {
        appSettings.putInt("role", value.value)
    }

    /**
     * Observes the filter role setting for changes, and runs the given callback
     * after the setting was changed.
     *
     * IMPORTANT: A strong reference to the returned SettingsListener must be held, otherwise
     * updates might not be sent! (https://github.com/russhwolf/multiplatform-settings#listeners)
     *
     * @param callback function to run when role was changed.
     * @return reference to the SettingsListener
     */
    override suspend fun observeRole(callback: (FilterRole) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener("role", FilterRole.default.value) {
            callback(FilterRole.fromInt(it))
        }
    }

    /**
     * Returns a flow of Filter Value from settings
     * @return flow<String>
     */
    override fun getFilterValueFlow(): Flow<String>
        = appSettings.getStringFlow("filter", "")

    /**
     * Returns the Filter Value from settings
     * @return string
     */
    override suspend fun getFilterValue(): String {
        return appSettings.getString("filter", "")
    }

    /**
     * Stores the Filter value in settings
     * @param value value to store
     */
    override suspend fun setFilterValue(value: String) {
        appSettings.putString("filter", value)
    }

    /**
     * Observes the filter value setting for changes, and runs the given callback
     * after the setting was changed.
     *
     * IMPORTANT: A strong reference to the returned SettingsListener must be held, otherwise
     * updates might not be sent! (https://github.com/russhwolf/multiplatform-settings#listeners)
     *
     * @param callback function to run when value was changed.
     * @return reference to the SettingsListener
     */
    override suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addStringListener("filter", "") { callback(it) }
    }

    /**
     * Returns a flow for push notification enablement from settings
     * @return Flow<PushState>
     */
    override fun getPushFlow(): Flow<PushState>
        = appSettings.getIntFlow("push", PushState.default.value).map { PushState.fromInt(it) }

    /**
     * Returns the push notification enablement from settings
     * @return PushState
     */
    override suspend fun getPush(): PushState {
        return PushState.fromInt(
            appSettings.getInt("push", PushState.DISABLED.value)
        )
    }

    /**
     * Stores the push notification enablement in settings
     * @param value PushState
     */
    override suspend fun setPush(value: PushState) {
        appSettings.putInt("push", value.value)
    }

    /**
     * Observes the push notification enablement setting for changes, and runs the given callback
     * after the setting was changed.
     *
     * IMPORTANT: A strong reference to the returned SettingsListener must be held, otherwise
     * updates might not be sent! (https://github.com/russhwolf/multiplatform-settings#listeners)
     *
     * @param callback function to run when PushState was changed.
     * @return reference to the SettingsListener
     */
    override suspend fun observePush(callback: (PushState) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener("push", PushState.default.value) {
            callback(PushState.fromInt(it))
        }
    }
}