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

import androidx.compose.runtime.NoLiveLiterals
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModel
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import io.github.aakira.napier.Napier
import it.skrape.core.htmlDocument
import it.skrape.matchers.toBePresentTimes
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Suppress("EXPECT_override_CLASSIFIERS_ARE_IN_BETA_WARNING")
@NoLiveLiterals
class JavaWebsiteParser : GsWebsiteParser() {
    override suspend fun parseSubstitutionTable(result: String): Result<SubstitutionApiModelSet>  {
        return htmlDocument(result) {
            var dateText: String? = null
            findFirst("td[class*=vpUeberschr]") {
                if(this.text.isNotEmpty())
                    dateText = this.text.trim()
            }

            if(dateText == "Beschilderung beachten!")
                return@htmlDocument Result.failure<SubstitutionApiModelSet>(
                    HolidayException()
                )

            var noteText = ""
            findFirst("td[class=vpTextLinks]") {
                if(this.text.isNotEmpty())
                    noteText = this.text.removePrefix("Hinweis:").trim()
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
                        klassFilter = processKlassForFilter(data[0]),
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

    override suspend fun parseTeachersNumPages(html: String): Int {
        return htmlDocument(html) {
            findFirst("table[class=\"eAusgeben\"] > tbody > tr:last-child > td > a:nth-last-child(2)") {
                this.attribute("href")
                    .substringAfter("seite=")
                    .toInt()
            }
        }
    }

    override suspend fun parseTeachers(
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

    override suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>> {
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
                            return@findFirst if(this.text.isNotEmpty()) {
                                mealName = this.text.trim()
                                true //<- We've got mealtxt
                            } else {
                                //TODO: This happens in holidays!
                                Napier.w { "[loadFoodPlan]: Got food with empty name, on html: ${meal.html}" }
                                false //<- No mealtxt, continue with next meal
                            }
                        }) return@forEach //Continue with next meal if no mealtxt

                    meal.findFirst("sub") {
                        mealAdditives = this
                            .text
                            .replace("\\s".toRegex(), "")
                            .split(",")
                            .toList()
                    }

                    if(!foods.containsKey(mealDate)) foods[mealDate] = mutableListOf()
                    foods[mealDate]?.add(
                        Food(
                            num = mealId,
                            name = mealName,
                            additives = mealAdditives
                        )
                    )
                } catch(e: Exception) {
                    Napier.w { e.stackTraceToString() }
                    return@htmlDocument Result.failure(e)
                }
            }

            Result.success(foods.entries.associate { it.key to it.value.toList() })
        }
    }

    private fun additiveShortLongOrderMismatched(text: String, shouldBeShort: Boolean): Boolean {
        return if (text.trim().endsWith(")").not() &&
            text.trim().length > 3) {
            //probably not the shortcode
            if (shouldBeShort) { //but should be -> mismatch
                Napier.w { "[ParseAdditives]: MISMATCH BETWEEN isShort AND TEXT " +
                        "(upper): $text" }
                true
            } else
                false
        } else {
            if (!shouldBeShort) {
                Napier.w { "[ParseAdditives]: MISMATCH BETWEEN isShort AND TEXT " +
                        "(lower): $text" }
                true
            } else
                false
        }
    }

    override suspend fun parseAdditives(html: String): Result<Map<String, String>> {
        val additiveMap = mutableMapOf<String, String>()
        return try {
            htmlDocument(html) {
                findAll("ingredients dl").forEach { additives ->
                    var isShort = true
                    var shortVal = ""
                    additives.children.forEach forAdditive@{
                        if(additiveShortLongOrderMismatched(it.text, isShort))
                            return@forAdditive


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
            Napier.w { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    /**
     * Checks if the [DocElement] has a child element matching the given [selector].
     * If SkrapeIt has a better solution for this, please tell me!
     * @param selector The selector to check for
     * @return True if the element has a child element matching the selector, false otherwise
     */
    private fun DocElement.hasElement(selector: String): Boolean {
        return try {
            this.findFirst(selector).isPresent
        } catch(enf: ElementNotFoundException) {
            false
        }
    }

    /**
     * Parses the date header of the exam table.
     * @param docElement The table row containing the date header
     * @param possibleYears The possible years of the exam table as parsed using Regex. Must contain exactly 3 items
     *                      (0=full match, 1=first year, 2=second year)
     * @return List of LocalDates for the following exam rows
     */
    private fun parseExamDatesHeader(docElement: DocElement, possibleYears: List<String>): List<LocalDate> {
        assert(possibleYears.size == 3) { "Years array must contain 3 items!" }

        //find all dates in table headings (td class=kopf) that have text
        return docElement
            .findAll("td[class=kopf]")
            .filter { one -> one.text.isNotBlank() }
            .map { dateHeader ->
                // We only need the first date of the week (the one before the line break)
                val weekStartDate: String = dateHeader.html.substringBefore("<br>")

                // Parse the german date-string using Regex to get the month and day
                val protoDate = Regex("([0-3]?[0-9])\\.([0-1]?[0-9])\\.")
                    .find(weekStartDate)
                    ?.groupValues ?: listOf("01", "01")

                // Choose year by month (if month is in first "half" of year, use first year, else second year)
                val protoYear = if (protoDate[2].toInt() in 8..12)
                    possibleYears[1] //First half of school year
                else
                    possibleYears[2] //Second half of school year

                LocalDate(
                    year = protoYear.toInt(),
                    monthNumber = protoDate[2].toInt(),
                    dayOfMonth = protoDate[1].toInt()
                )
            }
    }

    private fun parseExamCell(cell: DocElement,
                              startOfCurrentWeek: LocalDate,
                              dayOffset: Int,
                              course: ExamCourse,
                              exams: MutableList<Exam>) {
        if (cell.text.isNotEmpty()) {
            //There are some exams in this cell!
            val examDay = startOfCurrentWeek.plus(dayOffset, DateTimeUnit.DAY)
            if (Regex("[^a-zA-Z0-9\\s]").matches(cell.text) ||
                cell.text == "Ferien"
            ) {
                // This is a holiday, skip it!
                // It does not happen in the exam plan that was up when writing this code,
                // but apparently it was like that in the past.
                return
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
                    if (examLbl.length < 6) //There *should* be no else cases
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
    }

    /**
     * Parses the exam table from the website.
     *
     * First, the date header is parsed using [parseExamDatesHeader].
     * Then, the table is parsed row by row (week by week, for single weekday)
     * column by column (weekday for weekday).
     *
     * @param html The html of the exam table
     * @param course The course of the exam table
     * @return List of parsed exams or Exception
     */
    override suspend fun parseExams(
        html: String,
        course: ExamCourse
    ): Result<List<Exam>> {
        val exams: MutableList<Exam> = mutableListOf()

        return try {
            htmlDocument(html) {
                val header = findFirst("td[class*=ueberschr]").html

                //years[0] = "2023/2024", [1] = 2023, [2] = 2024
                val years = Regex("([0-9]{4})/([0-9]{4})").find(header)?.groupValues ?: emptyList()

                assert(years.size == 3) { "Years array must contain 3 items!" }

                var dates: List<LocalDate>? = null

                var currentWeekIndex: Int //= current Column
                var dayOffset: Int = -1 //= current Row

                findAll("tr").forEach forRow@{
                    if (it.hasElement("td[class*=\"ueberschr\"]")) {
                        //We've found the top-most header row, no need to parse columns...
                        return@forRow //...just continue with the next row
                    }

                    if (it.hasElement("td[class*=\"kopf\"]")) {
                        //This is the "second" header row containing the dates, parse them!
                        dates = parseExamDatesHeader(it, years)

                        //Reset dayOffset-counter
                        dayOffset = 0
                        return@forRow
                    }

                    if (it.hasElement("td[class*=\"ferien\"]")) {
                        //This is overridely the second table, below the exam plan.
                        //We can now stop parsing the table.
                        return@forRow
                    }

                    // Reset week index, we're parsing a new row (= new weekday)
                    currentWeekIndex = 0
                    it.findAll("td").forEach forCell@{ cell ->
                        if (cell.classNames.contains("tag")) {
                            //This is the first column containing date names. Skip that.
                            return@forCell
                        }

                        assert(dates != null) { "Dates array doesn't exist, this should not happen!" }

                        parseExamCell(
                            cell = cell,
                            startOfCurrentWeek = dates!![currentWeekIndex],
                            dayOffset = dayOffset,
                            course = course,
                            exams = exams
                        )

                        // Increment to next week (= next column)
                        currentWeekIndex++
                    }

                    //We've parsed all weeks for this weekday, increment weekday offset
                    // and continue with next weekday (= next row)
                    dayOffset++
                }

                exams.sortBy { it.date }

                Result.success(exams)
            }
        } catch (ex: Exception) {
            Napier.w { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }
}