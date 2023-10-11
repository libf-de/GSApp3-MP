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

import cocoapods.HTMLKit.HTMLDocument
import cocoapods.HTMLKit.HTMLElement
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.ElementNotFoundException
import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.exceptions.InvalidElementTypeException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModel
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import platform.Foundation.allKeys
import platform.Foundation.allValues
import platform.Foundation.array


actual class GsWebsiteParser {
    actual suspend fun parseSubstitutionTable(result: String): Result<SubstitutionApiModelSet> {
        val doc = HTMLDocument.documentWithString(result)

        val dateHead: HTMLElement? = doc.querySelector("td[class*=vpUeberschr]")
        var dateText = "(kein Datum)"
        if(dateHead != null)
            if(!dateHead.isEmpty())
                dateText = dateHead.textContent
        if(dateText == "Beschilderung beachten!") return Result.failure(HolidayException())

        val noteHead: HTMLElement? = doc.querySelector("td[class=vpTextLinks]")
        var noteText = ""
        if(noteHead != null)
            if(!noteHead.isEmpty())
                noteText = noteHead.textContent

        val substitutionApiModels = ArrayList<SubstitutionApiModel>()
        var colNum: Int
        var data: Array<String>
        var isNew: Boolean
        val subElements = doc
            .querySelectorAll("tr[id=Svertretungen], tr[id=Svertretungen] ~ tr")
            .ifEmpty {
                doc.querySelector("td[class*=vpTextZentriert]")
                    ?.parentElement
                    ?.setElementId("firstSubstitution")
                doc.querySelectorAll("#firstSubstitution, #firstSubstitution ~ tr")
            }.ifEmpty {
                return Result.failure(
                    ElementNotFoundException("tr[id=Svertretungen] or #firstSubstitution")
                )
            }

        for (elem: Any? in subElements) {
            if(elem !is HTMLElement)
                return Result.failure(InvalidElementTypeException("Element in subElements is not" +
                        "a HTMLElement!"))

            colNum = 0
            data = arrayOf("", "", "", "", "", "", "")
            isNew = elem.outerHTML.contains("<strong>")

            elem.childNodes.array.forEach {
                if(it !is HTMLElement)
                    return Result.failure(InvalidElementTypeException("Cell in elem is not" +
                            "a HTMLElement!"))
                data[colNum] = it.textContent.trim()
                colNum++
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

        return Result.success(
            SubstitutionApiModelSet(
            date = dateText,
            notes = noteText,
            substitutionApiModels = substitutionApiModels
        )
        )
    }

    actual suspend fun parseTeachersNumPages(html: String): Int {
        val doc = HTMLDocument.documentWithString(html)
        return try {
            val lastRow = doc.querySelectorAll("table[class=\"eAusgeben\"] > tbody > tr")
                .last() as HTMLElement
            val attrMap = lastRow.querySelector("td > a:nth-last-child(2)")?.attributes!!
            val hrefAttr = attrMap.allValues.get(attrMap.allKeys.indexOf("href")) as String
            hrefAttr.substringAfter("seite=").toInt()
        } catch(ex: Exception) {
            ex.printStackTrace()
            0
        }

    }

    actual suspend fun parseTeachers(
        html: String,
        list: MutableList<Teacher>
    ) {
        val doc = HTMLDocument.documentWithString(html)
        for(row: Any? in doc.querySelectorAll("table.eAusgeben > tbody > tr")) {
            if(row !is HTMLElement) {
                println(
                    "Row in document is not" +
                            "a HTMLElement!"
                )
                return
            }

            val teacherDetails = row
                .querySelector("td.eEintragGrau,td.eEintragWeiss") ?: continue
            if(!teacherDetails.innerHTML.contains("<br>")) continue
            list.add(
                Teacher(
                longName = teacherDetails.innerHTML.substringBefore("<br>").trim(),
                shortName = teacherDetails.innerHTML.substringAfter("Kürzel:").trim()
            )
            )
        }
    }

    actual suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>> {
        val foods = mutableMapOf<LocalDate, MutableList<Food>>()
        val doc = HTMLDocument.documentWithString(html)
        for(meals: Any? in doc.querySelectorAll("table#menu-table_KW td[mealid]")) {
            try {
                if(meals !is HTMLElement) {
                    return Result.failure(ElementNotFoundException("meals not a HTMLElement!"))
                }

                val attrMap = meals.attributes
                val mealDate = LocalDate.parse(
                    attrMap.allValues[attrMap.allKeys.indexOf("day")] as String)
                val mealId = (attrMap.allValues[attrMap.allKeys.indexOf("mealid")] as String).toInt()
                val mealName: String = meals.querySelector("span[id=mealtext]")?.textContent ?: ""
                val mealAdditives: List<String> =
                    (meals.querySelector("sub")?.textContent ?: "")
                        .replace(Regex("\\s"), "")
                        .split(",")
                        .toList()

                if(!foods.containsKey(mealDate)) foods[mealDate] = mutableListOf()
                foods[mealDate]!!.add(
                    Food(
                        num = mealId,
                        name = mealName,
                        additives = mealAdditives
                    )
                )
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

        return Result.success(foods.entries.associate { it.key to it.value.toList() })
    }

    actual suspend fun parseAdditives(html: String): Result<Map<String, String>> {
        val additiveMap = mutableMapOf<String, String>()
        return try {
            val doc = HTMLDocument.documentWithString(html)
            for(addiveColumn: Any? in doc.querySelectorAll("ingredients dl")) {
                if(addiveColumn !is HTMLElement) {
                    return Result.failure(ElementNotFoundException("additiveColumn not a HTMLElement!"))
                }

                var isShort = true
                var shortVal = ""
                addiveColumn.childNodes.array.forEach { item ->
                    if (item !is HTMLElement)
                        return Result.failure(
                            InvalidElementTypeException("Additive item is not a HTMLElement!")
                        )


                    if (!item.textContent.trim().endsWith(")") && item.textContent.trim().length > 3) {
                        //probably not the shortcode
                        if (isShort) {
                            println("[ParseAdditives]: MISSMATCH BETWEEN isShort AND TEXT (upper): ${item.outerHTML}")
                            return@forEach
                        }
                    } else {
                        if (!isShort) {
                            println("[ParseAdditives]: MISSMATCH BETWEEN isShort AND TEXT (lower): ${item.outerHTML}")
                            return@forEach
                        }
                    }


                    if (isShort) {
                        shortVal = item.textContent.trim().removeSuffix(")")
                        isShort = false
                    } else {
                        additiveMap[shortVal] = item.textContent.trim()
                        isShort = true
                    }
                }
            }

            Result.success(additiveMap)
        } catch(ex: Exception) {
            ex.printStackTrace()
            Result.failure(ex)
        }
    }

    actual suspend fun parseExams(html: String, course: ExamCourse): Result<Map<LocalDate, List<Exam>>> {
        val exams: MutableList<Exam> = mutableListOf()
        val germanDateWithoutYearRegex = Regex("([0-3]?[0-9])\\.([0-1]?[0-9])\\.")

        return try {
            val doc = HTMLDocument.documentWithString(html)

            var header: HTMLElement = doc.querySelector("td[class*=ueberschr]") ?:
                            return Result.failure(ElementNotFoundException("header not found :("))

            val years = Regex("([0-9]{4})/([0-9]{4})").find(header.innerHTML)?.groupValues ?: emptyList()

            if (years.size != 3)
                println("WARNING: Years array size != 3!!")

            var dates: List<LocalDate>? = null

            var currentColumn: Int
            var dayOffset: Int = -1 //= currentRow

            doc.querySelectorAll("tr").forEach forRow@{ tableRow ->
                if(tableRow !is HTMLElement) {
                    return Result.failure(ElementNotFoundException("a tableRow not a HTMLElement!"))
                }

                if(tableRow.querySelector("td[class*=\"ueberschr\"]") != null) {
                    //We've found the header row, no need to parse columns...
                    return@forRow //...just continue with the next row
                }

                val headElements = tableRow.querySelectorAll("td[class=kopf]")
                                           .filterIsInstance<HTMLElement>()
                if(headElements.isNotEmpty()) {
                    //This is the "second" header row containing the dates, parse them!
                    dates = headElements.filter { it.textContent.isNotBlank() }.map { dateHeader ->
                        val weekStartDate: String = dateHeader.innerHTML.split("<br>")[0] //TODO: Doof.
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

                if (tableRow.querySelector("td[class*=\"ferien\"]") != null) {
                    //This is actually the second table, below the exam plan.
                    //Theoretically, we could now end table parsing, but for the sake of it
                    //continue. TODO: Stop.
                    return@forRow
                }

                currentColumn = 0
                tableRow.querySelectorAll("td")
                    .filterIsInstance<HTMLElement>()
                    .forEach forCell@{ cell ->
                        if (dates == null) {
                            println("dates array does not exist, this should not happen -> dateHeader was not found!")
                            return Result.failure(ElementNotFoundException(
                                "dates array does not exist, this should not happen -> dateHeader was not found!"
                            ))
                        }

                        if (cell.classList.contains("tag")) {
                            //This is the first column containing date names. Skip that.
                            return@forCell
                        }

                        if (cell.textContent.isNotEmpty()) {
                            //There are some exams in this cell!
                            val examDay = dates!![currentColumn].plus(dayOffset, DateTimeUnit.DAY)
                            if (Regex("[^a-zA-Z0-9\\s]").matches(cell.textContent) ||
                                cell.textContent == "Ferien"
                            ) {
                                //TODO: Is there anything to do here? Continue??
                            } else if (cell.textContent == "Abgabe SF") {
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
                                cell.textContent.trim().split(" ").forEach { examLbl ->
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

                dayOffset++;
                }

            exams.sortBy { it.date }

            Result.success(exams.groupBy { it.date })
        } catch(ex: Exception) {
            ex.printStackTrace()
            Result.failure(ex)
        }
    }
}