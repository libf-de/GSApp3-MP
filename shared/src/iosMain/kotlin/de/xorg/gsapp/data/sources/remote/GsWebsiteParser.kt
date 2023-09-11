package de.xorg.gsapp.data.sources.remote

import cocoapods.HTMLKit.HTMLDocument
import cocoapods.HTMLKit.HTMLElement
import de.xorg.gsapp.data.exceptions.ElementNotFoundException
import de.xorg.gsapp.data.exceptions.HolidayException
import de.xorg.gsapp.data.exceptions.InvalidElementTypeException
import de.xorg.gsapp.data.exceptions.NoEntriesException
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import platform.Foundation.NSString
import platform.Foundation.allKeys
import platform.Foundation.allValues
import platform.Foundation.array


actual class GsWebsiteParser {
    actual suspend fun parseSubstitutionTable(result: String): Result<SubstitutionSet> {
        val doc = HTMLDocument.documentWithString(result)

        val dateHead: HTMLElement? = doc.querySelector("td[class*=vpUeberschr]")
        var dateText: String = "(kein Datum)"
        if(dateHead != null)
            if(!dateHead.isEmpty())
                dateText = dateHead.textContent
        if(dateText == "Beschilderung beachten!") return Result.failure(HolidayException())

        val noteHead: HTMLElement? = doc.querySelector("td[class=vpTextLinks]")
        var noteText: String = ""
        if(noteHead != null)
            if(!noteHead.isEmpty())
                noteText = noteHead.textContent

        val substitutions = ArrayList<Substitution>()
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

            substitutions.add(
                Substitution(
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
            SubstitutionSet(
            date = dateText,
            notes = noteText,
            substitutions = substitutions
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

    actual suspend fun parseFoodOffers(html: String): Result<List<FoodOffer>> {
        return Result.failure(NoEntriesException())
    }
}