package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.LocalDate

class MockParser : GsWebsiteParser() {
    override suspend fun parseSubstitutionTable(result: String): Result<SubstitutionApiModelSet> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun parseTeachersNumPages(html: String): Int {
        return 0
    }

    override suspend fun parseTeachers(html: String, list: MutableList<Teacher>) { }

    override suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun parseAdditives(html: String): Result<Map<String, String>> {
        return Result.failure(Exception("Not implemented"))
    }

    override suspend fun parseExams(html: String, course: ExamCourse): Result<List<Exam>> {
        return Result.failure(Exception("Not implemented"))
    }

}