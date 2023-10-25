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

package de.xorg.gsapp.data.sources.local

import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
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
    fun getLatestSubstitutionHashAndDate(): Result<Pair<Int, LocalDate>>
    fun getLatestSubstitutionHash(): Result<Int>
    suspend fun addSubstitutionPlan(value: SubstitutionApiModelSet)
    suspend fun cleanupSubstitutionPlan()

    fun getFoodMapFlow(): Flow<Result<Map<LocalDate, List<Food>>>>
    fun getFoodsForDateFlow(date: LocalDate): Flow<Result<List<Food>>>
    fun getFoodDatesFlow(): Flow<Result<List<LocalDate>>>
    fun getLatestFoods(): Result<List<Food>>
    fun getLatestFoodDate(): Result<LocalDate>
    suspend fun addFoodMap(value: Map<LocalDate, List<Food>>)
    suspend fun cleanupFoodPlan()
    fun getAdditivesFlow(): Flow<Result<Map<String, String>>>
    suspend fun addAllAdditives(value: Map<String, String>)

    fun getExamsFlow(): Flow<Result<List<Exam>>>
    suspend fun getAllExams(): Result<List<Exam>>
    suspend fun addAllExams(value: List<Exam>)
    suspend fun clearAndAddAllExams(value: List<Exam>)
    suspend fun cleanupExams()
    suspend fun deleteExam(toDelete: Exam)

    fun getSubjectsFlow(): Flow<Result<List<Subject>>>
    suspend fun addAllSubjects(value: List<Subject>)
    suspend fun addSubject(value: Subject)
    suspend fun updateSubject(value: Subject)
    suspend fun deleteSubject(value: Subject)
    suspend fun resetSubjects(value: List<Subject>)
    suspend fun subjectExists(shortName: String): Boolean
    suspend fun countSubjects(): Result<Long>


    fun getTeachersFlow(): Flow<Result<List<Teacher>>>
    suspend fun addAllTeachers(value: List<Teacher>)
    suspend fun addTeacher(value: Teacher)
    suspend fun updateTeacher(value: Teacher)
    suspend fun deleteTeacher(value: Teacher)



}