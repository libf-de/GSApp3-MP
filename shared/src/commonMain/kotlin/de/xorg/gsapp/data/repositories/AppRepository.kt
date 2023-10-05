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
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.local.SqldelightDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.time.measureTime

class AppRepository(di: DI) : GSAppRepository {
    private val apiDataSource: RemoteDataSource by di.instance()
    private val localDataSource: LocalDataSource by di.instance()
    private val sqlDataSource: SqldelightDataSource by di.instance()
    private val appSettings: Settings by di.instance()



    private val apiSubstitutionsFlow: Flow<Result<SubstitutionApiModelSet>> = flow {
        emit(apiDataSource.loadSubstitutionPlan())
    }

    private fun getWebSubstitutions(): Flow<Result<SubstitutionSet>> = combine(apiSubstitutionsFlow,
        teachers, subjects) { subs, teachers, subjects ->
        subs.map {
            SubstitutionSet(
                date = it.date,
                notes = it.notes,
                substitutions = it.substitutionApiModels.map { sub ->
                    Substitution(
                        primitive = sub,
                        origSubject = findSubjectInResult(subjects, sub.origSubject),
                        substTeacher = findTeacherInResult(teachers, sub.substTeacher),
                        substSubject = findSubjectInResult(subjects, sub.substSubject)
                    )
                }.groupBy { subs -> subs.klass }
            )
        }
    }

    override suspend fun getSubstitutions(reload: Boolean): Flow<Result<SubstitutionSet>> = flow {
        var cached: Result<SubstitutionSet>? = null
        if(!reload) {
            val dbTime = measureTime {
                cached = sqlDataSource.loadSubstitutionPlan()
            }
            val jsonTime = measureTime {
                localDataSource.loadSubstitutionPlan()
            }
            println("db => ${dbTime.inWholeMilliseconds}ms, json => ${jsonTime.inWholeMilliseconds}ms")

            if(cached?.isSuccess == true) emit(cached!!)
        }

        getWebSubstitutions().collect { web ->
            if(web.isSuccess) {
                if(!reload && cached != null) {
                    if(cached!!.isSuccess && cached!!.getOrNull() == web.getOrNull())
                        return@collect //Do nothing if web is same as cache
                }
                val dbStoreTime = measureTime {
                    sqlDataSource.storeSubstitutionPlan(web.getOrNull()!!)
                }
                val jsonStoreTime = measureTime {
                    localDataSource.storeSubstitutionPlan(web.getOrNull()!!)
                }
                println("db => ${dbStoreTime.inWholeMilliseconds}ms, json => ${jsonStoreTime.inWholeMilliseconds}ms")
                //sqlDataSource.storeSubstitutionPlan(web.getOrNull()!!) //Store web plan in cache
                emit(web) // & emit web
            } else if(!reload && cached != null) {
                if (!cached!!.isSuccess) emit(web) //Emit web error if there's no cache
            }
        }
    }

    override val subjects: Flow<Result<List<Subject>>> = flow {
        val cached = sqlDataSource.loadSubjects()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadSubjects()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull() == web.getOrNull())
                    return@flow
            sqlDataSource.storeSubjects(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    private fun findTeacherInResult(results: Result<List<Teacher>>, value: String): Teacher {
        if(value.isBlank()) return Teacher("", "(kein)")
        return results.getOrNull()?.firstOrNull {
                s -> s.shortName.lowercase() == value.lowercase()
        } ?: Teacher(value)
    }

    private fun findSubjectInResult(results: Result<List<Subject>>, value: String): Subject {
        if(value.isBlank()) return Subject("", "(kein)", Color.Black)

        return results.getOrNull()?.firstOrNull {
                s -> s.shortName.lowercase() == value.lowercase()
        } ?: Subject(value)
    }

    override suspend fun addSubject(value: Subject): Result<Boolean> {
        val local = sqlDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.add(value)
        sqlDataSource.storeSubjects(newSubjects)
        return Result.success(success)
    }

    override suspend fun deleteSubject(value: Subject): Result<Boolean> {
        val local = sqlDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.remove(value)
        return Result.success(success)
    }

    override suspend fun updateSubject(oldSub: Subject, newSub: Subject): Result<Subject> {
        val local = sqlDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.set(newSubjects.indexOf(oldSub), newSub)
        return Result.success(success)
    }

    override val teachers: Flow<Result<List<Teacher>>> = flow {
        val cached = sqlDataSource.loadTeachers()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadTeachers()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull() == web.getOrNull())
                    return@flow
            val mergedTeacherList = (web.getOrNull() ?: emptyList()) +
                    (cached.getOrNull() ?: emptyList())
            sqlDataSource.storeTeachers(mergedTeacherList)
            emit(Result.success(mergedTeacherList))
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    override suspend fun addTeacher(value: Teacher): Result<Boolean> {
        val local = sqlDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newTeachers = local.getOrNull()!!.toMutableList()
        val success = newTeachers.add(value)
        sqlDataSource.storeTeachers(newTeachers)
        return Result.success(success)
    }

    override suspend fun deleteTeacher(value: Teacher): Result<Boolean> {
        val local = sqlDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newTeachers = local.getOrNull()!!.toMutableList()
        val success = newTeachers.remove(value)
        sqlDataSource.storeTeachers(newTeachers)
        return Result.success(success)
    }

    override suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher> {
        val local = sqlDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.set(newSubjects.indexOf(oldTea), newTea)
        return Result.success(success)
    }

    private val apiFoodPlan: Flow<Result<Map<LocalDate, List<Food>>>> = flow {
        val cached: Result<Map<LocalDate, List<Food>>> = sqlDataSource.loadFoodPlan()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadFoodPlan()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            sqlDataSource.storeFoodPlan(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    override val additives: Flow<Result<Map<String, String>>> = flow {
        val cached = sqlDataSource.loadAdditives()
        if(cached.isSuccess) emit(cached)

        val web = apiDataSource.loadAdditives()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            sqlDataSource.storeAdditives(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

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

    /****/
    override suspend fun getRole(): FilterRole {
        return FilterRole.fromInt(
            appSettings.getInt("role", FilterRole.ALL.value)
        )
    }
    override suspend fun setRole(value: FilterRole) {
        appSettings.putInt("role", value.value)
    }

    override suspend fun observeRole(callback: (FilterRole) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener("role", FilterRole.default.value) {
            callback(FilterRole.fromInt(it))
        }
    }

    override suspend fun getFilterValue(): String {
        return appSettings.getString("filter", "")
    }
    override suspend fun setFilterValue(value: String) {
        appSettings.putString("filter", value)
    }

    override suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addStringListener("filter", "") { callback(it) }
    }

    override suspend fun getPush(): PushState {
        return PushState.fromInt(
            appSettings.getInt("push", PushState.DISABLED.value)
        )
    }
    override suspend fun setPush(value: PushState) {
        appSettings.putInt("push", value.value)
    }

    override suspend fun observePush(callback: (PushState) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener("push", PushState.default.value) {
            callback(PushState.fromInt(it))
        }
    }
}