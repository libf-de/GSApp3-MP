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

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.UnexpectedStatusCodeException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import org.kodein.di.DI
import org.lighthousegames.logging.logging


/**
 * This fetches data from official websites
 * (https://www.gymnasium-sonneberg.de and http://schulkueche-bestellung.de)
 * using Ktor (https://github.com/ktorio/ktor).
 *
 * It utilizes a platform-specific GsWebsiteParser, as there's currently
 * no Kotlin Multiplatform HTML parsing library that supports Android, iOS and Desktop.
 * This could be merged when such a library exists in the future, although this separation
 * could be used to properly test the parser.
 */

open class WebsiteDataSource(di: DI) : RemoteDataSource {
    companion object {
        val log = logging()
    }

    private val parser = GsWebsiteParser()
    private val client = HttpClient()

    private var foodplanHtmlCache: String? = null

    override suspend fun getSubstitutionPlan(): Result<SubstitutionApiModelSet> {
        try {
            val response: HttpResponse =
                client.get("https://www.gymnasium-sonneberg.de/Informationen/vp.php5")
            if (response.status.value !in 200..299) {
                log.e { "loadSubstitutionPlan(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            return try {
                parser.parseSubstitutionTable(response.body())
            } catch (ex: Exception) {
                log.e { "inner ex: ${ex.stackTraceToString()}" }
                Result.failure(ex)
            }
        } catch(ex2: Exception) {
            log.e { "outer ex2: ${ex2.stackTraceToString()}" }
            return Result.failure(ex2)
        }
    }

    override suspend fun getTeachers(): Result<List<Teacher>> {
        try {
            val response: HttpResponse = client.get(
                urlString = "https://www.gymnasium-sonneberg.de/Kontakt/Sprech/ausgeben.php5?seite=1"
            )

            if (response.status.value !in 200..299) {
                log.e { "loadTeachers(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            val teachers = ArrayList<Teacher>()
            parser.parseTeachers(response.body(), teachers)

            for (page: Int in 2..parser.parseTeachersNumPages(response.body())) {
                val nextResponse: HttpResponse = client.get(
                    urlString = "https://www.gymnasium-sonneberg.de/Kontakt/Sprech/ausgeben.php5?seite="
                            + page.toString()
                )

                if (response.status.value !in 200..299) {
                    log.e { "loadTeachers(): (subpage) Unexpected code: $response" }
                    return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
                }

                parser.parseTeachers(nextResponse.body(), teachers)
            }

            return Result.success(teachers)
        } catch(ex: Exception) {
            log.e { ex.stackTraceToString() }
            return Result.failure(ex)
        }
    }

    override suspend fun getFoodplan(): Result<Map<LocalDate, List<Food>>> {
        try {
            val response: HttpResponse = client.get("https://schulkueche-bestellung.de/de/menu/14")

            if (response.status.value !in 200..299) {
                log.e { "loadFoodPlan(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            foodplanHtmlCache = response.body()
            return parser.parseFoodOffers(response.body())
        } catch (e: Exception) {
            log.e { e.stackTraceToString() }
            return Result.failure(e)
        }
    }

    override suspend fun getAdditives(): Result<Map<String, String>> {
        try {
            foodplanHtmlCache?.let {
                return parser.parseAdditives(it)
            }

            val response: HttpResponse = client.get("https://schulkueche-bestellung.de/de/menu/14")

            if (response.status.value !in 200..299) {
                log.e { "loadAdditives(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            return parser.parseAdditives(response.body())
        } catch (ex: Exception){
            log.e { ex.stackTraceToString() }
            return Result.failure(ex)
        }
    }

    override suspend fun getFoodplanAndAdditives(): Result<Pair<Map<LocalDate, List<Food>>, Map<String, String>>> {
        try {
            val response: HttpResponse = client.get("https://schulkueche-bestellung.de/de/menu/14")

            if (response.status.value !in 200..299) {
                log.e { "loadFoodPlan(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            val additives = parser.parseAdditives(response.body())
            val foodPlan = parser.parseFoodOffers(response.body())

            if(additives.isFailure)
                return Result.failure(
                    additives.exceptionOrNull() ?: Exception("unknown cause (parseAdditives)"))
            if(foodPlan.isFailure)
                return Result.failure(
                    foodPlan.exceptionOrNull() ?: Exception("unknown cause (parseFoodplan)"))

            return Result.success(Pair(foodPlan.getOrNull()!!, additives.getOrNull()!!))
        } catch (e: Exception) {
            log.e { e.stackTraceToString() }
            return Result.failure(e)
        }
    }

    override suspend fun getExams(): Result<List<Exam>> {
        val examList: MutableList<Exam> = mutableListOf()
        ExamCourse.entries.forEach {
            val subExams = getExamsForCourse(it)
            if(subExams.isSuccess)
                examList.addAll(subExams.getOrNull()!!)
            else
                log.w { "Failed to load exams for course ${it.name}: ${subExams.exceptionOrNull()}" }
        }
        return Result.success(examList)
    }
    private suspend fun getExamsForCourse(course: ExamCourse): Result<List<Exam>> {
        try {
            val response: HttpResponse = client.get(
                "https://www.gymnasium-sonneberg.de/Schueler/KursArb/ka.php5?seite=${course.ordinal}")

            if (response.status.value !in 200..299) {
                log.e { "loadExams(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            return parser.parseExams(response.body(), course)
        } catch (e: Exception) {
            log.e { e.stackTraceToString() }
            return Result.failure(e)
        }
    }

}
