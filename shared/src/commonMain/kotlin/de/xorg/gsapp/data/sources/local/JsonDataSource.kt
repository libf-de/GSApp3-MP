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

package de.xorg.gsapp.data.sources.local

import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.EmptyStoreException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.datetime.LocalDate

/**
 * This uses multiple JSON files for local storage.
 * Will probably be removed in favour of database storage.
 *
 * + simple, stored in cache folder (not good for subjects!), probably fast enough
 * - not nice
 *
 * Although I have to admit, there was often no big speed difference with the little
 * tests I did using a huge substitution plan!
 */

class JsonDataSource(private var pathSrc: PathSource) : LocalDataSource {

    private var substitutionStore: KStore<SubstitutionSet> = storeOf(
        filePath = pathSrc.getSubstitutionPath())
    private var subjectsStore: KStore<List<Subject>> = storeOf(
        filePath = pathSrc.getSubjectsPath())
    private var teachersStore: KStore<List<Teacher>> = storeOf(
        filePath = pathSrc.getTeachersPath())
    private var foodplanStore: KStore<Map<LocalDate, List<Food>>> = storeOf(
        filePath = pathSrc.getFoodplanPath())
    private var additivesStore: KStore<Map<String, String>> = storeOf(
        filePath = pathSrc.getAdditivesPath())

    override suspend fun loadSubstitutionPlan(): Result<SubstitutionSet> {
        println("loading from ${pathSrc.getSubstitutionPath()}")
        val mayStored: SubstitutionSet?
        try {
            mayStored = substitutionStore.get()
        } catch(ex: Exception) {
            return Result.failure(ex)
        }
        return if(mayStored != null)
            Result.success(mayStored)
        else
            Result.failure(EmptyStoreException())
    }

    override suspend fun storeSubstitutionPlan(value: SubstitutionSet) {
        try {
            substitutionStore.set(value)
        } catch(ex: Exception) {
            //Log.w("jsonDataSource", "Failed to store substitution plan, stack trace:")
            ex.printStackTrace()
        }
    }

    override suspend fun loadSubjects(): Result<List<Subject>> {
        val mayStored: List<Subject>?
        try {
            mayStored = subjectsStore.get()
        } catch(ex: Exception) {
            return Result.failure(ex)
        }
        return if(mayStored != null)
            Result.success(mayStored)
        else
            Result.failure(EmptyStoreException())
    }

    override suspend fun storeSubjects(value: List<Subject>) {
        try {
            subjectsStore.set(value)
        } catch(ex: Exception) {
            //Log.w("jsonDataSource", "Failed to store substitution plan, stack trace:")
            ex.printStackTrace()
        }
    }

    override suspend fun loadTeachers(): Result<List<Teacher>> {
        val mayStored: List<Teacher>?
        try {
            mayStored = teachersStore.get()
        } catch(ex: Exception) {
            return Result.failure(ex)
        }
        return if(mayStored != null)
            Result.success(mayStored)
        else
            Result.failure(EmptyStoreException())
    }

    override suspend fun storeTeachers(value: List<Teacher>) {
        try {
            teachersStore.set(value)
        } catch(ex: Exception) {
            //Log.w("jsonDataSource", "Failed to store substitution plan, stack trace:")
            ex.printStackTrace()
        }
    }

    override suspend fun loadFoodPlan(): Result<Map<LocalDate, List<Food>>> {
        val mayStored: Map<LocalDate, List<Food>>?
        try {
            mayStored = foodplanStore.get()
        } catch(ex: Exception) {
            return Result.failure(ex)
        }
        return if(mayStored != null)
            Result.success(mayStored)
        else
            Result.failure(EmptyStoreException())
    }

    override suspend fun storeFoodPlan(value: Map<LocalDate, List<Food>>) {
        try {
            foodplanStore.set(value)
        } catch(ex: Exception) {
            //Log.w("jsonDataSource", "Failed to store substitution plan, stack trace:")
            ex.printStackTrace()
        }
    }

    override suspend fun loadAdditives(): Result<Map<String, String>> {
        val mayStored: Map<String, String>?
        try {
            mayStored = additivesStore.get()
        } catch(ex: Exception) {
            return Result.failure(ex)
        }
        return if(mayStored != null)
            Result.success(mayStored)
        else
            Result.failure(EmptyStoreException())
    }

    override suspend fun storeAdditives(value: Map<String, String>) {
        try {
            additivesStore.set(value)
        } catch(ex: Exception) {
            //Log.w("jsonDataSource", "Failed to store substitution plan, stack trace:")
            ex.printStackTrace()
        }
    }

    override suspend fun loadExams(course: ExamCourse): Result<Map<LocalDate, List<Exam>>> {
        TODO("Not yet implemented")
    }

    override suspend fun storeExams(value: Map<LocalDate, List<Exam>>) {
        TODO("Not yet implemented")
    }
}