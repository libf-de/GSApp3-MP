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
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Offline-first data source.
 *
 * Must provide read functions that return flows for all data stores,
 * and matching write suspend functions to update the stores from web.
 *
 * Interface for device-local data sources.
 * Must provide read *and write* functions for all data stores.
 */
interface LocalDataSource {
    fun getSubstitutionPlanFlow(): Flow<Result<SubstitutionSet>>
    suspend fun addSubstitutionPlan(value: SubstitutionSet)
    suspend fun cleanupSubstitutionPlan()

    fun getSubjectsFlow(): Flow<Result<List<Subject>>>
    suspend fun addAllSubjects(value: List<Subject>)
    suspend fun addSubject(value: Subject)
    suspend fun updateSubject(value: Subject)
    suspend fun deleteSubject(value: Subject)
    suspend fun subjectExists(shortName: String): Boolean

    fun getTeachersFlow(): Flow<Result<List<Teacher>>>
    suspend fun addAllTeachers(value: List<Teacher>)
    suspend fun addTeacher(value: Teacher)
    suspend fun updateTeacher(value: Teacher)
    suspend fun deleteTeacher(value: Teacher)


    fun getFoodMapFlow(): Flow<Result<Map<LocalDate, List<Food>>>>
    fun getFoodsForDateFlow(date: LocalDate): Flow<Result<List<Food>>>
    fun getFoodDatesFlow(): Flow<Result<List<LocalDate>>>
    suspend fun addFood(value: Food)
    suspend fun addAllFoods(value: List<Food>)
    suspend fun updateFood(value: Food)
    suspend fun deleteFood(value: Food)
    suspend fun cleanupFoodPlan()

    fun getAdditivesFlow(): Flow<Result<Map<String, String>>>
    suspend fun storeAdditives(value: Map<String, String>)

    fun getExamsFlow(): Flow<Result<Map<LocalDate, List<Exam>>>>
    suspend fun storeExams(value: Map<LocalDate, List<Exam>>)
    suspend fun cleanupExams()
    suspend fun deleteExam(toDelete: Exam)
}