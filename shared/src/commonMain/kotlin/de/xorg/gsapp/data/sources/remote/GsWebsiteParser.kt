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

package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import org.lighthousegames.logging.logging

/**
 * This is the platform-specific parser that parses html for all data types.
 * Get's called from GsWebsiteDataSource.
 */
expect class GsWebsiteParser() {
    suspend fun parseSubstitutionTable(result: String): Result<SubstitutionApiModelSet>

    suspend fun parseTeachersNumPages(html: String): Int

    suspend fun parseTeachers(html: String, list: MutableList<Teacher>)

    suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>>

    suspend fun parseAdditives(html: String): Result<Map<String, String>>

    suspend fun parseExams(html: String, course: ExamCourse): Result<List<Exam>>

}

private val germanMonthsMap = mapOf(
    "januar" to Month.JANUARY,
    "februar" to Month.FEBRUARY,
    "märz" to Month.MARCH,
    "april" to Month.APRIL,
    "mai" to Month.MAY,
    "juni" to Month.JUNE,
    "juli" to Month.JULY,
    "august" to Month.AUGUST,
    "september" to Month.SEPTEMBER,
    "oktober" to Month.OCTOBER,
    "november" to Month.NOVEMBER,
    "dezember" to Month.DECEMBER
)

fun parseSubstitutionDate(dateStr: String?): LocalDate {
    if(dateStr == null) return LocalDate.fromEpochDays(0)

    val titleDateRegex = Regex("[a-zA-Z]+,\\s+den\\s+([0-9]+).\\s+([a-zA-Z]+)\\s+([0-9]+)")
    val dateParts = titleDateRegex.find(dateStr)?.groupValues ?: listOf(
        "",
        "01",
        "Januar",
        "2000"
    )
    if(dateParts.size != 4) {
        logging().e { "parseSubstitutionDate(): dateParts size != 4!" }
        return LocalDate.fromEpochDays(0)
    }

    return try {
        LocalDate(
            year = dateParts[3].toInt(),
            month = germanMonthsMap[dateParts[2].lowercase()]!!,
            dayOfMonth = dateParts[1].toInt()
        )
    } catch (ex: Exception) {
        logging().e { ex.stackTraceToString() }
        return LocalDate.fromEpochDays(0)
    }

}