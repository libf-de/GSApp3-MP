package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.SubstitutionApiModel
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import it.skrape.core.htmlDocument
import it.skrape.matchers.toBePresentTimes
import it.skrape.selects.DocElement
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

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

            var noteText: String = ""
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

    actual suspend fun parseFoodOffers(html: String): Result<List<FoodOffer>> {
        val foods = mutableMapOf<String, MutableList<Food>>()
        return htmlDocument(html) {
            var kw = 0 //TODO: Remove or implement
            var dateFrom: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            var dateTo: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            findFirst("button#time-selector-dropdown") {
                kw = this.text.split("\\|\\|".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0].replace("\\D".toRegex(), "").toInt()
                dateFrom = LocalDate.parse(
                    this.text
                        .split("\\|\\|".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1]
                        .split("-".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[0]
                        .trim { it <= ' ' } +
                            this.text
                                .split("\\|\\|".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1]
                                .split("\\.".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                                .last()
                )
                dateTo = LocalDate.parse(
                    this.text
                        .split("\\|\\|".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1]
                        .split("-".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1]
                        .trim { it <= ' ' }
                )
            }

            findAll("table#menu-table_KW td[mealid]") {
                forEach { meal ->
                    val mealDate = meal.attribute("day")
                    val mealId = meal.attribute("mealid").toInt()
                    var mealName = ""
                    var mealAdditives = emptyList<String>()

                    meal.findFirst("span[id=mealtext]") {
                        if(this.text.isNotEmpty())
                            mealName = this.text.trim()
                        else
                            println("[loadFoodPlan]: Got food with empty name, on html: ${meal.html}")
                    }

                    meal.findFirst("sub") {
                        mealAdditives = this.text.trim().split(",").toList()
                    }

                    if(!foods.containsKey(mealDate)) foods[mealDate] = mutableListOf()
                    foods[mealDate]!!.add(
                        Food(
                            num = mealId,
                            name = mealName,
                            additives = mealAdditives
                        )
                    )
                }
            }

            Result.success(
                foods.entries.map {
                    FoodOffer(
                        dataFromDate=dateFrom,
                        dataTillDate=dateTo,
                        date= LocalDate.parse(it.key),
                        foods=it.value
                    )
                }
            )
        }
    }
}