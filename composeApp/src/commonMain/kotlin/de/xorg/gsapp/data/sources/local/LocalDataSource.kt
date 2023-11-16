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
    /**
     * Returns a flow for the substitution plan from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<SubstitutionSet>>
     */
    fun getSubstitutionPlanFlow(): Flow<SubstitutionSet>

    /**
     * Returns the latest SubstitutionSet hash and date from local storage.
     * @return Result<Pair<Int, LocalDate>>
     */
    fun getLatestSubstitutionHashAndDate(): Result<Pair<Int, LocalDate>>

    /**
     * Returns the SubstitutionSet id for a given date string.
     * @param dateStr DateString
     * @return Result<Long?> SubstitutionSet id or null if not found
     */
    fun findSubstitutionSetIdByDateString(dateStr: String): Result<Long?>

    /**
     * Adds a new SubstitutionSet to local storage.
     * @param value SubstitutionSet to add
     */
    suspend fun addSubstitutionPlanAndCleanup(value: SubstitutionApiModelSet)

    /**
     * Adds a new SubstitutionSet to local storage.
     * @param value SubstitutionSet to add
     */
    suspend fun addSubstitutionPlan(value: SubstitutionApiModelSet)

    /**
     * Clears past SubstitutionSets from local storage.
     */
    suspend fun cleanupSubstitutionPlan()


    /**
     * Returns a flow for the food plan from local storage as a map of date to list of food.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<Map<LocalDate, List<Food>>>>
     */
    fun getFoodMapFlow(): Flow<Map<LocalDate, List<Food>>>

    /**
     * Returns a flow for foods for the given date from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @param date Date to get foods for
     * @return Flow<Result<List<Food>>> Foods for the given date
     */
    fun getFoodsForDateFlow(date: LocalDate): Flow<Result<List<Food>>>

    /**
     * Returns a flow for the list of dates for which food is available from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<List<LocalDate>>> List of dates for which food is available
     */
    fun getFoodDatesFlow(): Flow<Result<List<LocalDate>>>

    /**
     * Returns the latest food plan from local storage as a list.
     * Will return a NoLocalDataException if no local data is available.
     * @return Result<List<Food>> Latest food plan
     */
    fun getLatestFoods(): Result<List<Food>>

    /**
     * Returns the latest food plan date from local storage.
     * Will return start of epoch if no local data is available.
     * @return Result<LocalDate> Latest food plan date
     */
    fun getLatestFoodDate(): Result<LocalDate>

    /**
     * Adds a new food plan to local storage.
     * @param value Map of date to list of food
     */
    suspend fun addFoodMap(value: Map<LocalDate, List<Food>>)

    /**
     * Clears past foods from local storage.
     */
    suspend fun cleanupFoodPlan()

    /**
     * Returns a flow for the list of additives from local storage.
     * Will return empty map if no local data is available.
     * @return Flow<Result<Map<String, String>>> List of additives
     */
    fun getAdditivesFlow(): Flow<Map<String, String>>

    /**
     * Adds new additives to local storage.
     * @param value Map of additives
     */
    suspend fun addAllAdditives(value: Map<String, String>)


    /**
     * Returns a flow for the list of exams from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<List<Exam>>> List of exams
     */
    fun getExamsFlow(): Flow<List<Exam>>

    /**
     * Returns the latest exams from local storage. Won't emit updates as not a flow!
     * @return Result<List<Exam>> Latest exams
     */
    suspend fun getAllExams(): Result<List<Exam>>

    /**
     * Adds new exams to local storage.
     * @param value List of exams
     */
    suspend fun addAllExams(value: List<Exam>)

    /**
     * Clears all exams and adds new ones from/to local storage.
     * @param value List of exams
     */
    suspend fun clearAndAddAllExams(value: List<Exam>)

    /**
     * Clears past exams from local storage.
     */
    suspend fun cleanupExams()

    /**
     * Deletes a given exam from local storage.
     * @param toDelete Exam to delete
     */
    suspend fun deleteExam(toDelete: Exam)


    /**
     * Returns a flow for the list of subjects from local storage.
     * @return Flow<Result<List<Subject>>> List of subjects
     */
    fun getSubjectsFlow(): Flow<Result<List<Subject>>>

    /**
     * Adds new subjects to local storage.
     * @param value List of subjects
     */
    suspend fun addAllSubjects(value: List<Subject>)

    /**
     * Adds a new subject to local storage.
     * @param value Subject to add
     */
    suspend fun addSubject(value: Subject)

    /**
     * Edits a subject in local storage.
     * Subject shortName must already exist in database.
     * @param value Subject to edit
     */
    suspend fun updateSubject(value: Subject)

    /**
     * Deletes a subject from local storage.
     * @param value Subject to delete
     */
    suspend fun deleteSubject(value: Subject)

    /**
     * Deletes all subjects and adds new ones from/to local storage.
     * @param value List of subjects
     */
    suspend fun resetSubjects(value: List<Subject>)

    /**
     * Returns whether there is a subject with the given shortName
     * @param shortName ShortName to check
     * @return Boolean Whether subject exists
     */
    suspend fun subjectExists(shortName: String): Boolean

    /**
     * Returns the number of subjects in local storage.
     * @return Result<Long> Number of subjects
     */
    suspend fun countSubjects(): Result<Long>

    /**
     * Returns a flow for the list of teachers from local storage.
     * @return Flow<Result<List<Teacher>>> List of teachers
     */
    fun getTeachersFlow(): Flow<Result<List<Teacher>>>

    /**
     * Adds new teachers to local storage.
     * @param value List of teachers
     */
    suspend fun addAllTeachers(value: List<Teacher>)

    /**
     * Adds a new teacher to local storage.
     * @param value Teacher to add
     */
    suspend fun addTeacher(value: Teacher)

    /**
     * Edits a teacher in local storage.
     * Teacher shortName must already exist in database.
     * @param value Teacher to edit
     */
    suspend fun updateTeacher(value: Teacher)

    /**
     * Deletes a teacher from local storage.
     * @param value Teacher to delete
     */
    suspend fun deleteTeacher(value: Teacher)
}