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
import de.xorg.gsapp.data.model.Additive
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sources.local.JsonDataSource
import de.xorg.gsapp.data.sources.remote.GsWebsiteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class AppRepository(
    private val webDataSource: GsWebsiteDataSource,
    private val jsonDataSource: JsonDataSource
) : GSAppRepository {
    //TODO: Maybe use Mutex variables to cache objects here for write operations

    // Substitution functions
    private val substitutions: Flow<Result<SubstitutionApiModelSet>> = flow {
        val cached = jsonDataSource.loadSubstitutionPlan()
        if(cached.isSuccess) emit(cached)

        val web = webDataSource.loadSubstitutionPlan()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull() == web.getOrNull())
                    return@flow
            jsonDataSource.storeSubstitutionPlan(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    override fun getSubstitutions(): Flow<Result<SubstitutionSet>> = combine(substitutions,
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

    private val subjects: Flow<Result<List<Subject>>> = flow {
        val cached = jsonDataSource.loadSubjects()
        if(cached.isSuccess) emit(cached)

        val web = webDataSource.loadSubjects()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull() == web.getOrNull())
                    return@flow
            jsonDataSource.storeSubjects(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
        /*val local = jsonDataSource.loadSubjects()
        if(local.isSuccess) {
            emit(local)
            return@flow
        }

        emit(Result.failure(Exception("No valid DataSource :(")))*/
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
        val local = jsonDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.add(value)
        jsonDataSource.storeSubjects(newSubjects)
        return Result.success(success)
    }

    override suspend fun deleteSubject(value: Subject): Result<Boolean> {
        val local = jsonDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.remove(value)
        return Result.success(success)
    }

    override suspend fun updateSubject(oldSub: Subject, newSub: Subject): Result<Subject> {
        val local = jsonDataSource.loadSubjects()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.set(newSubjects.indexOf(oldSub), newSub)
        return Result.success(success)
    }

    private val teachers: Flow<Result<List<Teacher>>> = flow {
        val cached = jsonDataSource.loadTeachers()
        if(cached.isSuccess) emit(cached)

        val web = webDataSource.loadTeachers()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull() == web.getOrNull())
                    return@flow
            val mergedTeacherList = (web.getOrNull() ?: emptyList()) +
                    (cached.getOrNull() ?: emptyList())
            jsonDataSource.storeTeachers(mergedTeacherList)
            emit(Result.success(mergedTeacherList))
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    override suspend fun addTeacher(value: Teacher): Result<Boolean> {
        val local = jsonDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newTeachers = local.getOrNull()!!.toMutableList()
        val success = newTeachers.add(value)
        jsonDataSource.storeTeachers(newTeachers)
        return Result.success(success)
    }

    override suspend fun deleteTeacher(value: Teacher): Result<Boolean> {
        val local = jsonDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newTeachers = local.getOrNull()!!.toMutableList()
        val success = newTeachers.remove(value)
        jsonDataSource.storeTeachers(newTeachers)
        return Result.success(success)
    }

    override suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher> {
        val local = jsonDataSource.loadTeachers()
        if(local.isFailure) return Result.failure(local.exceptionOrNull()!!)

        val newSubjects = local.getOrNull()!!.toMutableList()
        val success = newSubjects.set(newSubjects.indexOf(oldTea), newTea)
        return Result.success(success)
    }

    override val foodPlan: Flow<Result<Map<LocalDate, List<Food>>>> = flow {
        val cached: Result<Map<LocalDate, List<Food>>> = jsonDataSource.loadFoodPlan()
        if(cached.isSuccess) emit(cached)

        val web = webDataSource.loadFoodPlan()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            jsonDataSource.storeFoodPlan(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }

    override val additives: Flow<Result<List<Additive>>> = flow {
        val cached = jsonDataSource.loadAdditives()
        if(cached.isSuccess) emit(cached)

        val web = webDataSource.loadAdditives()
        if(web.isSuccess) {
            //TODO: Can I compare results directly?
            if(cached.isSuccess)
                if(cached.getOrNull()!! == web.getOrNull()!!)
                    return@flow
            jsonDataSource.storeAdditives(web.getOrNull()!!)
            emit(web)
        } else if (cached.isSuccess) { return@flow }

        emit(web)
    }
}