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
import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModel
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import it.skrape.core.htmlDocument
import it.skrape.matchers.toBePresentTimes
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.lighthousegames.logging.logging


actual class GsWebsiteParser {
    companion object {
        val log = logging()
    }

    actual suspend fun parseSubstitutionTable(result: String): Result<SubstitutionApiModelSet>  {
        return htmlDocument(result) {
            var dateText = "(kein Datum)"
            findFirst("td[class*=vpUeberschr]") {
                if(this.text.isNotEmpty())
                    dateText = this.text.trim()
            }

            if(dateText == "Beschilderung beachten!") Result.failure<SubstitutionApiModelSet>(
                HolidayException()
            )

            var noteText = ""
            findFirst("td[class=vpTextLinks]") {
                if(this.text.isNotEmpty())
                    noteText = this.text.replace("Hinweis:", "").trim()
            }

            val substitutionApiModels = ArrayList<SubstitutionApiModel>()
            var colNum: Int
            var data: Array<String>
            var isNew: Boolean

            val substElements: List<DocElement>
                    = this.findAll("tr[id=Svertretungen], tr[id=Svertretungen] ~ tr").ifEmpty {
                val parent = this.findFirst("td[class*=vpTextZentriert]").parent
                this.findAll("${parent.ownCssSelector}, ${parent.ownCssSelector} ~ tr")
            }

            substElements.forEach { currentRow ->
                colNum = 0
                data = arrayOf("", "", "", "", "", "", "")
                isNew = currentRow.html.contains("<strong>")

                currentRow.children {
                    toBePresentTimes(7)
                    forEach {
                        data[colNum] = it.text.trim()
                        colNum++
                    }
                }

                substitutionApiModels.add(
                    SubstitutionApiModel(
                        klass = data[0],
                        lessonNr = data[1],
                        origSubject = data[2],
                        substTeacher = data[3],
                        substRoom = data[4],
                        substSubject = data[5],
                        notes = data[6],
                        isNew = isNew
                    )
                )
            }

            Result.success(SubstitutionApiModelSet(
                dateStr = dateText,
                date = parseSubstitutionDate(dateText),
                notes = noteText,
                substitutionApiModels = substitutionApiModels
            ))

        }
    }

    actual suspend fun parseTeachersNumPages(html: String): Int {
        return htmlDocument(html) {
            findFirst("table[class=\"eAusgeben\"] > tbody > tr:last-child > td > a:nth-last-child(2)") {
                this.attribute("href")
                    .substringAfter("seite=")
                    .toInt()
            }
        }
    }

    actual suspend fun parseTeachers(
        html: String,
        list: MutableList<Teacher>
    ) {
        return htmlDocument(html) {
            findAll("table.eAusgeben > tbody > tr:not(:first-child,:last-child)").forEach {
                it.findFirst("td.eEintragGrau,td.eEintragWeiss") {
                    if(!this.html.contains("<br>")) return@findFirst
                    val teacherName = this.html.split("<br>")[0]
                    val teacherShort = this.html.substringAfter("Kürzel:").trim()
                    list.add(
                        Teacher(
                            longName = teacherName,
                            shortName = teacherShort
                        )
                    )
                }

            }
        }
    }

    actual suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>> {
        val foods = mutableMapOf<LocalDate, MutableList<Food>>()
        return htmlDocument(html) {
            findAll("table#menu-table_KW td[mealid]") .forEach { meal ->
                try {
                    val mealDate = LocalDate.parse(meal.attribute("day"))
                    val mealId = meal.attribute("mealid").toInt()
                    var mealName = ""
                    var mealAdditives = emptyList<String>()

                    //Look for a mealtxt span - if we found one with text -> assign to mealName
                    if(!meal.findFirst("span[id=mealtext]") {
                        if(this.text.isNotEmpty()) {
                            mealName = this.text.trim()
                            return@findFirst true //<- We've got mealtxt
                        } else {
                            //TODO: This happens in holidays!
                            log.w { "[loadFoodPlan]: Got food with empty name, on html: ${meal.html}" }
                            return@findFirst false //<- No mealtxt, continue with next meal
                        }
                    }) return@forEach //Continue with next meal if no mealtxt

                    meal.findFirst("sub") {
                        mealAdditives = this.text.replace("\\s".toRegex(), "").split(",").toList()
                    }

                    if(!foods.containsKey(mealDate)) foods[mealDate] = mutableListOf()
                    foods[mealDate]!!.add(
                        Food(
                            num = mealId,
                            name = mealName,
                            additives = mealAdditives
                        )
                    )
                } catch(e: Exception) {
                    log.w { e.stackTraceToString() }
                    return@htmlDocument Result.failure(e)
                }
            }

            Result.success(foods.entries.associate { it.key to it.value.toList() })
        }
    }

    actual suspend fun parseAdditives(html: String): Result<Map<String, String>> {
        val additiveMap = mutableMapOf<String, String>()
        return try {
            htmlDocument(html) {
                findAll("ingredients dl").forEach { additives ->
                    var isShort = true
                    var shortVal = ""
                    additives.children.forEach forAdditive@{
                        if (!it.text.trim().endsWith(")") && it.text.trim().length > 3) {
                            //probably not the shortcode
                            if (isShort) {
                                log.w { "[ParseAdditives]: MISSMATCH BETWEEN isShort AND TEXT " +
                                        "(upper): ${it.html}" }
                                return@forAdditive
                            }
                        } else {
                            if (!isShort) {
                                log.w { "[ParseAdditives]: MISSMATCH BETWEEN isShort AND TEXT " +
                                        "(lower): ${it.html}" }
                                return@forAdditive
                            }
                        }


                        if (isShort) {
                            shortVal = it.text.trim().removeSuffix(")")
                            isShort = false
                        } else {
                            additiveMap[shortVal] = it.text.trim()
                            isShort = true
                        }
                    }
                }
                Result.success(additiveMap)
            }
        } catch(ex: Exception) {
            log.w { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    //TODO: Has SkrapeIt a better solution for this??
    private fun DocElement.hasElement(selector: String): Boolean {
        return try {
            this.findFirst(selector).isPresent
        } catch(enf: ElementNotFoundException) {
            false
        }
    }

    actual suspend fun parseExams(html: String, course: ExamCourse): Result<Map<LocalDate, List<Exam>>> {
        val exams: MutableList<Exam> = mutableListOf()
        val germanDateWithoutYearRegex = Regex("([0-3]?[0-9])\\.([0-1]?[0-9])\\.")

        return try {
            htmlDocument(html) {
                val header = findFirst("td[class*=ueberschr]").html

                //years[0] = "2023/2024", [1] = 2023, [2] = 2024
                val years = Regex("([0-9]{4})/([0-9]{4})").find(header)?.groupValues ?: emptyList()

                if (years.size != 3) log.w { "WARNING: Years array size != 3!!" }

                findAll("td[class=kopf] ~ td").map { dateHeader -> //find all dates in table headings
                    val weekStart: String = dateHeader.html.split("<br>")[0] //TODO: Doof.
                    val protoDate =
                        germanDateWithoutYearRegex.find(weekStart)?.groupValues ?: listOf(
                            "01",
                            "01"
                        )
                    val protoYear = if (protoDate[2].toInt() in 8..12) years[1] else years[2]

                    LocalDate(
                        year = protoYear.toInt(),
                        monthNumber = protoDate[2].toInt(),
                        dayOfMonth = protoDate[1].toInt()
                    )
                }


                var dates: List<LocalDate>? = null

                var currentColumn: Int
                var dayOffset: Int = -1 //= currentRow

                findAll("tr").forEach forRow@{
                    if (it.hasElement("td[class*=\"ueberschr\"]")) {
                        //We've found the header row, no need to parse columns...
                        return@forRow //...just continue with the next row TODO: dont set day to 0
                    }

                    if (it.hasElement("td[class*=\"kopf\"]")) {
                        //This is the "second" header row containing the dates, parse them!
                        dates = it.findAll("td[class=kopf]").filter { one ->
                            one.text.isNotBlank()
                        }.map { dateHeader -> //find all dates in table headings
                            val weekStartDate: String = dateHeader.html.split("<br>")[0] //TODO: Doof.
                            val protoDate = germanDateWithoutYearRegex
                                             .find(weekStartDate)?.groupValues ?: listOf("01", "01")
                            val protoYear = if (protoDate[2].toInt() in 8..12)
                                                years[1] //First half of school year
                                            else
                                                years[2] //Second half of school year

                            LocalDate(
                                year = protoYear.toInt(),
                                monthNumber = protoDate[2].toInt(),
                                dayOfMonth = protoDate[1].toInt()
                            )
                        }

                        dayOffset = 0
                        return@forRow
                    }

                    if (it.hasElement("td[class*=\"ferien\"]")) {
                        //This is actually the second table, below the exam plan.
                        //Theoretically, we could now end table parsing, but for the sake of it
                        //continue. TODO: Stop.
                        return@forRow
                    }

                    currentColumn = 0
                    it.findAll("td").forEach forCell@{ cell ->
                        if (dates == null) {
                            log.w { "dates array does not exist, this should not happen -> " +
                                    "dateHeader was not found!" }
                            return@htmlDocument Result.failure(ElementNotFoundException(
                                "dates array does not exist, this should not happen -> " +
                                        "dateHeader was not found!"
                            ))
                        }

                        if (cell.classNames.contains("tag")) {
                            //This is the first column containing date names. Skip that.
                            return@forCell
                        }

                        if (cell.text.isNotEmpty()) {
                            //There are some exams in this cell!
                            val examDay = dates!![currentColumn].plus(dayOffset, DateTimeUnit.DAY)
                            if (Regex("[^a-zA-Z0-9\\s]").matches(cell.text) ||
                                cell.text == "Ferien"
                            ) {
                                //TODO: Is there anything to do here? Continue??
                            } else if (cell.text == "Abgabe SF") {
                                //This is submission of seminar papers, add it!
                                exams.add(
                                    Exam(
                                        label = "Abgabe SF",
                                        date = examDay,
                                        course = course
                                    )
                                )
                            } else {
                                //This is an exam(s) cell, add them/it to the list!
                                cell.text.trim().split(" ").forEach { examLbl ->
                                    if (examLbl.length < 6) //TODO: Are there any "else" cases??
                                        exams.add(
                                            Exam(
                                                label = examLbl.trim(),
                                                date = examDay,
                                                course = course
                                            )
                                        )
                                }
                            }
                        }
                        currentColumn++
                    }
                    dayOffset++
                }

                exams.sortBy { it.date }

                Result.success(
                    exams.groupBy { it.date }
                )
            }
        } catch (ex: Exception) {
            log.w { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }
}