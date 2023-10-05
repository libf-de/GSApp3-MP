package de.xorg.gsapp.data.sources.remote

import cocoapods.HTMLKit.HTMLDocument
import cocoapods.HTMLKit.HTMLElement
import de.xorg.gsapp.data.exceptions.ElementNotFoundException
import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.exceptions.InvalidElementTypeException
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModel
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.LocalDate
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
}