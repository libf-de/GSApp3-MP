package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModel
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import it.skrape.core.htmlDocument
import it.skrape.matchers.toBePresentTimes
import it.skrape.selects.DocElement
import kotlinx.datetime.LocalDate

actual class GsWebsiteParser {
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
                date = dateText,
                notes = noteText,
                substitutionApiModels = substitutionApiModels
            ))

        }
    }

    actual suspend fun parseTeachersNumPages(html: String): Int {
        return htmlDocument(html) {
            findFirst("table[class=\"eAusgeben\"] > tbody > tr:last-child > td > a:nth-last-child(2)") {
                this
                    .attribute("href")
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
            findAll("table.eAusgeben > tbody > tr:not(:first-child,:last-child)") {
                forEach {
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
    }

    actual suspend fun parseFoodOffers(html: String): Result<Map<LocalDate, List<Food>>> {
        val foods = mutableMapOf<LocalDate, MutableList<Food>>()
        return htmlDocument(html) {
            findAll("table#menu-table_KW td[mealid]") {
                forEach { meal ->
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
                                println("[loadFoodPlan]: Got food with empty name, on html: ${meal.html}") //TODO: This happens in holidays!
                                return@findFirst false //<- No mealtxt, continue with next meal
                            }
                        }) return@forEach //Continue with next meal if no mealtxt

                        meal.findFirst("sub") {
                            mealAdditives = this.text.replace(Regex("\\s"), "")
                                                     .split(",").toList()
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
                        e.printStackTrace()
                    }
                }
            }

            Result.success(foods.entries.associate { it.key to it.value.toList() })
        }
    }

    actual suspend fun parseAdditives(html: String): Result<Map<String, String>> {
        val additiveMap = mutableMapOf<String, String>()
        return htmlDocument(html) {
            findAll("ingredients dl").forEach {
                var isShort = true
                var shortVal = ""
                it.children.forEach {
                    if(!it.text.trim().endsWith(")") && it.text.trim().length > 3) {
                        //probably not the shortcode
                        if(isShort) {
                            println("[ParseAdditives]: MISSMATCH BETWEEN isShort AND TEXT (upper): ${it.html}")
                            return@forEach
                        }
                    } else {
                        if(!isShort) {
                            println("[ParseAdditives]: MISSMATCH BETWEEN isShort AND TEXT (lower): ${it.html}")
                            return@forEach
                        }
                    }


                    if(isShort) {
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
    }
}