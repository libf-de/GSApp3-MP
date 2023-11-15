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
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * The App Repository is set up as an interface to be able to test the app easily, if I ever
 * find the time and motivation to write tests.
 * Maybe will revert to just the implementation if modifying both interface and implementation
 * continues to annoy me when writing new features.
 * ----
 * The repository shall contain functions to…
 * - load data synchronously from local, as Flow<> -> get_____()
 * - update the local data asynchronously from remote -> update_____()
 * - add new data, entered by the user -> add______()
 * - update existing data, edited by the user -> edit_____()
 * - delete local data -> delete______()
 */

interface GSAppRepository {
    fun getSubstitutions(): Flow<SubstitutionSet>
    suspend fun updateSubstitutions(callback: (Result<Boolean>) -> Unit)

    //Could switch to separate datelist + query foods per date?
    fun getFoodplan(): Flow<Result<Map<LocalDate, List<Food>>>>
    suspend fun updateFoodplan(callback: (Result<Boolean>) -> Unit)

    fun getExams(): Flow<Result<List<Exam>>>
    suspend fun updateExams(callback: (Result<Boolean>) -> Unit)
    // No Create, Update and Delete functions, as above types are not user-editable.

    fun getTeachers(): Flow<Result<List<Teacher>>>
    suspend fun updateTeachers(callback: (Result<Boolean>) -> Unit)
    suspend fun addTeacher(value: Teacher): Result<Boolean>
    suspend fun editTeacher(oldTea: Teacher, newLongName: String): Result<Teacher>
    suspend fun deleteTeacher(value: Teacher): Result<Boolean>

    fun getSubjects(): Flow<Result<List<Subject>>>
    suspend fun updateSubjects(force: Boolean = false, callback: (Result<Boolean>) -> Unit) //stub as currently subjects are not fetched from remote
    suspend fun addSubject(value: Subject): Result<Boolean>
    suspend fun deleteSubject(value: Subject): Result<Boolean>
    suspend fun resetSubjects(): Result<Boolean>
    suspend fun editSubject(subject: Subject, newLongName: String? = null, newColor: Color? = null): Result<Subject>
}