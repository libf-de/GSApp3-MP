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

import com.russhwolf.settings.SettingsListener
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * The App Repository is set up as an interface to be able to test the app easily, if I ever
 * find the time and motivation to write tests.
 * Maybe will revert to just the implementation if modifying both interface and implementation
 * continues to annoy me when writing new features.
 */

interface GSAppRepository {

    // Shall return a flow of a Result, containing a processed SubstitutionSet.
    // Shall only use remote sources and not emit anything if that fails if reload is true
    suspend fun getSubstitutions(reload: Boolean): Flow<Result<SubstitutionSet>>

    // Shall be a flow of a Result, containing the teacher list (from local, then remote)
    val teachers: Flow<Result<List<Teacher>>>

    // Shall insert the given teacher into local storage, and return whether that was successful
    // or the exception that occurred TODO: Not needed?
    suspend fun addTeacher(value: Teacher): Result<Boolean>

    // Shall delete the given teacher from local storage, and return whether that was successful
    // or the exception that occurred TODO: Not needed?
    suspend fun deleteTeacher(value: Teacher): Result<Boolean>

    // Shall update the given teacher in local storage, and return the old object or the exception
    // that occurred TODO: Not needed?
    suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher>

    // Shall be a flow of a Result, containing the subject list from local storage
    // If that's empty, it should be filled with defaults.
    val subjects: Flow<Result<List<Subject>>>

    // Shall insert the given subject into local storage, and return whether that was successful
    // or the exception that occurred
    suspend fun addSubject(value: Subject): Result<Boolean>

    // Shall delete the given subject from local storage, and return whether that was successful
    // or the exception that occurred
    suspend fun deleteSubject(value: Subject): Result<Boolean>

    // Shall update the given subject in local storage, and return the old object or the exception
    // that occurred
    suspend fun updateSubject(oldSub: Subject, newSub: Subject): Result<Subject>

    // Shall be a flow of a Result, mapping dates to their corresponding Lists of Food
    val foodPlan: Flow<Result<Map<LocalDate, List<Food>>>>

    // Shall be a flow of a Result, mapping additive short names to their long names.
    val additives: Flow<Result<Map<String, String>>>

    // Shall return a flow of a Result, mapping dates to their  corresponding Lists of Exam,
    // for the given ExamCourse. Shall only use remote sources and not emit anything if that fails
    // if reload is true
    suspend fun getExams(course: ExamCourse, reload: Boolean): Flow<Result<Map<LocalDate, List<Exam>>>>

    /**
     * These shall return the value stored in settings, store the given value in settings or
     * setup an observer that triggers when the setting was changed. See implementation
     * for further documentation
     */
    suspend fun getRole(): FilterRole
    suspend fun setRole(value: FilterRole)
    suspend fun observeRole(callback: (FilterRole) -> Unit): SettingsListener?

    suspend fun getFilterValue(): String
    suspend fun setFilterValue(value: String)
    suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener?

    suspend fun getPush(): PushState
    suspend fun setPush(value: PushState)
    suspend fun observePush(callback: (PushState) -> Unit): SettingsListener?
}