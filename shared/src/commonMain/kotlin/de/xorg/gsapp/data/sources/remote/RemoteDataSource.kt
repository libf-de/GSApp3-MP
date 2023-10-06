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

package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.LocalDate

interface RemoteDataSource {
    suspend fun loadSubstitutionPlan(): Result<SubstitutionApiModelSet>
    suspend fun loadSubjects(): Result<List<Subject>>
    suspend fun loadTeachers(): Result<List<Teacher>>

    suspend fun loadFoodPlan(): Result<Map<LocalDate, List<Food>>>
    suspend fun loadAdditives(): Result<Map<String, String>>

    suspend fun loadExams(course: ExamCourse): Result<Map<LocalDate, List<Exam>>>
}