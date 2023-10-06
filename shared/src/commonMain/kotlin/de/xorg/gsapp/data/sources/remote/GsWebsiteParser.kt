package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.LocalDate

expect class GsWebsiteParser() {
    suspend fun parseSubstitutionTable(result: String): Result<SubstitutionApiModelSet>

    suspend fun parseTeachersNumPages(html: String): Int

    suspend fun parseTeachers(html: String, list: MutableList<Teacher>)

    suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>>

    suspend fun parseAdditives(html: String): Result<Map<String, String>>

    suspend fun parseExams(html: String, course: ExamCourse): Result<Map<LocalDate, List<Exam>>>

}