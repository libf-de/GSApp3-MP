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
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.`sources-legacy`.local.LocalDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import org.kodein.di.DI
import org.kodein.di.instance
import org.lighthousegames.logging.logging
import kotlin.time.measureTime

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

class AppRepository(di: DI) : GSAppRepository {

    companion object {
        val log = logging()
    }

    //Get data sources from DI
    private val apiDataSource: RemoteDataSource by di.instance()
    private val localDataSource: LocalDataSource by di.instance()

    private val appSettings: Settings by di.instance()

    // This is used to create a flow from the "pure" web api, to be able to then
    // just combine the teachers and subjects flow with it. Maybe there is a better
    // way to achieve this :S
    private val apiSubstitutionsFlow: Flow<Result<SubstitutionApiModelSet>> = flow {
        emit(apiDataSource.loadSubstitutionPlan())
    }

    // Combines web substitutions, teachers and subjects flows to a flow that emits "non-api"
    // SubstitutionSets (with Substitutions) [replaces string teacher/subject with objects]
    private fun getWebSubstitutions(): Flow<Result<SubstitutionSet>> = combine(apiSubstitutionsFlow,
        teachers, subjects) { subs, teachers, subjects ->
        subs.map {
            SubstitutionSet(
                dateStr = it.dateStr,
                date = it.date,
                notes = it.notes,
                substitutions = it.substitutionApiModels.map { sub -> //replace strings->objects
                    Substitution(
                        primitive = sub,
                        origSubject = findSubjectInResult(subjects, sub.origSubject),
                        substTeacher = findTeacherInResult(teachers, sub.substTeacher),
                        substSubject = findSubjectInResult(subjects, sub.substSubject)
                    )
                }.groupBy { subs -> subs.klass } //Generate class-grouped map
            )
        }
    }

    // |GET| Substitutions
    // This is the "master" substitutions flow, combining web-api and local-cached sources.
    override suspend fun getSubstitutions(reload: Boolean): Flow<Result<SubstitutionSet>> = flow {
        var cached: Result<SubstitutionSet>? = null
        if(!reload) {
            //TODO: Remove; don't measure read time in release
            val dbTime = measureTime {
                cached = localDataSource.loadSubstitutionPlan()
            }
            log.d {"read db in ${dbTime.inWholeMilliseconds}ms" }

            if(cached?.isSuccess == true) {
                log.d {"emitting database"}
                emit(cached!!)
            }
        }

        getWebSubstitutions().collect { web ->
            log.d {"collected a web substitution"}
            if(web.isSuccess) {
                log.d {"web is success"}
                if(!reload && cached != null) {
                    log.d { "not reloading, cache exists"}
                    if(cached!!.isSuccess && cached!!.getOrNull() == web.getOrNull()) {
                        log.d {"web same as cache"}
                        return@collect //Do nothing if web is same as cache
                    }
                }

                //TODO: Remove; don't measure time in release builds
                val dbStoreTime = measureTime {
                    localDataSource.storeSubstitutionPlan(web.getOrNull()!!) //Store web plan in cache
                }
                log.d { "stored in db in ${dbStoreTime.inWholeMilliseconds}ms" }

                log.d {"emitting web"}
                emit(web) // & emit web
            } else if(!reload && cached != null) {
                log.d {"not reloading, cache exists :("}
                if (!cached!!.isSuccess) {
                    log.d {"emitting web error"}
                    emit(web) //Emit web error if there's no cache
                }
            }
        }
    }

    // |GET| Subjects - combines "remote" and local sources for subjects
    // As there is no subject list on the website, we'll provide some defaults.
    // I'll probably have to change the logic here to not overwrite the user's subjects
    // with the default values TODO: Review logic to prevent overwriting user settings
    override val subjects: Flow<Result<List<Subject>>> = flow {
        val local = localDataSource.loadSubjects()
        if(local.isSuccess) emit(local)

        val defaults = apiDataSource.loadSubjects()

        // If local subjects and default subjects were successful, merge them.
        if(defaults.isSuccess && local.isSuccess) {
            //TODO: Can I compare results directly?
            val localDb = local.getOrNull() ?: emptyList()
            val remoteDb = defaults.getOrNull() ?: emptyList()

            if(localDb.isEmpty()) // If there is no local configuration, store the defaults
                localDataSource.storeSubjects(remoteDb) // in the local database

            // Merge local and default subjects, preferring the subjects stored in Database
            val mergedSubjects = localDb + remoteDb.filterNot { it in localDb }
            emit(Result.success(mergedSubjects))
        } else if (defaults.isSuccess && local.isFailure) {
            log.w { "Failed to load subjects from local database, returning defaults!"}
            emit(defaults)
        } else if (defaults.isFailure && local.isFailure) {
            emit(local) //If both fail, return the local database error
        } else if (defaults.isFailure && local.isSuccess) { //We've already emitted local
            return@flow
        }
    }

    /**
     * Finds the Subject in the provided list for the given shortName string value
     * @param results List to be searched
     * @param value shortName to be searched for
     * @return matching Subject object
     * TODO: Use database properly!
     */
    private fun findSubjectInResult(results: Result<List<Subject>>, value: String): Subject {
        if(value.isBlank()) return Subject("", "(kein)", Color.Black)

        return results.getOrNull()?.firstOrNull {
                s -> s.shortName.lowercase() == value.lowercase()
        } ?: Subject(value)
    }

    /**
     * Adds a new subject to the local source.
     * @param value value to be added
     * @return Exception or boolean whether it was successful.
     * TODO: Use database properly!
     */
    override suspend fun addSubject(value: Subject): Result<Boolean> {
        val local = localDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.add(value)
        localDataSource.storeSubjects(newSubjects)
        return Result.success(success)
    }

    /**
     * Deletes a given subject from local source.
     * @param value subject to delete
     * @return Exception or boolean -> Successful
     * TODO: Use database properly!
     */
    override suspend fun deleteSubject(value: Subject): Result<Boolean> {
        val local = localDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.remove(value)
        return Result.success(success)
    }

    /**
     * Updates the given subject in local source.
     * @param oldSub Subject to be edited
     * @param newSub Subject with changes
     * @return old subject?
     * TODO: Use database properly!
     */
    override suspend fun updateSubject(
        oldSub: Subject,
        newLongName: String?,
        newColor: Color?
    ): Result<Subject> {
        val newSub = Subject(
            shortName = oldSub.shortName,
            longName = newLongName ?: oldSub.longName,
            color = newColor ?: oldSub.color
        )

        if(!oldSub.totallyEqual(newSub))
            try {
                localDataSource.updateSubject(newSub)
            } catch(ex: Exception) {
                return Result.failure(ex)
            }

        return Result.success(oldSub)
    }

    //****************** TEACHER ******************

    // |GET| Teachers
    override val teachers: Flow<Result<List<Teacher>>> = flow {
        val cached = localDataSource.loadTeachers()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadTeachers()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull() == web.getOrNull())
                    return@flow
            val mergedTeacherList = (web.getOrNull() ?: emptyList()) +
                    (cached.getOrNull() ?: emptyList())
            localDataSource.storeTeachers(mergedTeacherList)
            emit(Result.success(mergedTeacherList))
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    /**
     * Finds the Teacher in the provided list for the given shortName string value
     * @param results List to be searched
     * @param value shortName to be searched for
     * @return matching Teacher object
     * TODO: Use database properly!
     */
    private fun findTeacherInResult(results: Result<List<Teacher>>, value: String): Teacher {
        if(value.isBlank()) return Teacher("", "(kein)")
        return results.getOrNull()?.firstOrNull {
                s -> s.shortName.lowercase() == value.lowercase()
        } ?: Teacher(value)
    }

    /**
     * Adds a new teacher to the local source.
     * @param value value to be added
     * @return Exception or boolean whether it was successful.
     * TODO: Use database properly!
     */
    override suspend fun addTeacher(value: Teacher): Result<Boolean> {
        val local = localDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newTeachers = local.getOrNull()!!.toMutableList()
        val success = newTeachers.add(value)
        localDataSource.storeTeachers(newTeachers)
        return Result.success(success)
    }

    /**
     * Deletes a given teacher from local source.
     * @param value subject to delete
     * @return Exception or boolean -> Successful
     * TODO: Use database properly!
     */
    override suspend fun deleteTeacher(value: Teacher): Result<Boolean> {
        val local = localDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newTeachers = local.getOrNull()!!.toMutableList()
        val success = newTeachers.remove(value)
        localDataSource.storeTeachers(newTeachers)
        return Result.success(success)
    }

    /**
     * Updates the given subject in local source.
     * @param oldTea Subject to be edited
     * @param newTea Subject with changes
     * @return old subject?
     * TODO: Use database properly!
     */
    override suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher> {
        val local = localDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.set(newSubjects.indexOf(oldTea), newTea)
        return Result.success(success)
    }

    //****************** FOOD PLAN ******************

    // |GET| Gets the "pure" foodplan from the web api? TODO: Store processed foodplan in database OR process directly in database?
    private val apiFoodPlan: Flow<Result<Map<LocalDate, List<Food>>>> = flow {
        val cached: Result<Map<LocalDate, List<Food>>> = localDataSource.loadFoodPlan()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadFoodPlan()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            localDataSource.storeFoodPlan(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    // |GET| Additives
    override val additives: Flow<Result<Map<String, String>>> = flow {
        val cached = localDataSource.loadAdditives()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadAdditives()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            localDataSource.storeAdditives(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    // Replaces the short additives with long names in foodplan
    override val foodPlan: Flow<Result<Map<LocalDate, List<Food>>>> = combine(apiFoodPlan, additives)
    { apiFoodPlan, additives ->
        if(!additives.isSuccess)
            apiFoodPlan
        else {
            val adMap = additives.getOrNull()!!
            apiFoodPlan.mapCatching { outerMap ->
                outerMap.mapValues { foodMap ->
                    foodMap.value.map { foodEntry ->
                        foodEntry.copy(
                            additives = foodEntry.additives.map { adShort -> adMap[adShort] ?: adShort }
                        )
                    }
                }
            }
        }
    }


    //****************** EXAMS ******************

    // |GET| Exam flow for the given ExamCourse.
    // TODO: Should I use one flow for this?
    override suspend fun getExams(course: ExamCourse, reload: Boolean): Flow<Result<Map<LocalDate, List<Exam>>>> = flow {
        val cached = localDataSource.loadExams(course)
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadExams(course)
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            localDataSource.storeExams(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
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