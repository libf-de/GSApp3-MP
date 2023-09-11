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

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.exceptions.UnexpectedStatusCodeException
import de.xorg.gsapp.data.model.Additive
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse


class GsWebsiteDataSource : RemoteDataSource {

    private val TAG = "GsWebsiteDataSource"

    private val parser = GsWebsiteParser()

    /*@Throws(ArrayIndexOutOfBoundsException::class)
    private fun parseResponse(result: String): Result<SubstitutionSet> {
        return htmlDocument(result) {
            var dateText = "(kein Datum)"
            findFirst("td[class*=vpUeberschr]") {
                if(this.text.isNotEmpty())
                    dateText = this.text.trim()
            }

            if(dateText == "Beschilderung beachten!") Result.failure<SubstitutionSet>(HolidayException())

            var noteText: String = ""
            findFirst("td[class=vpTextLinks]") {
                if(this.text.isNotEmpty())
                    noteText = this.text.replace("Hinweis:", "").trim()
            }

            val substitutions = ArrayList<Substitution>()
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
                    ))
            }

            Result.success(SubstitutionSet(
                date = dateText,
                notes = noteText,
                substitutions = substitutions
            ))

        }
    }

    private fun fallbackLoad(result: String): Result<SubstitutionSet> {
        if (result == "E") return Result.failure(Exception("Result is E"))
        val substitutions = ArrayList<Substitution>()
        var dateStr = ""
        var noteStr = ""

        try {
            dateStr = result
                    .split("<td colspan=\"7\" class=\"rundeEckenOben vpUeberschr\">".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1]
                        .split("</td>".toRegex())
                        .dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0]
                        .replace("        ", "")
            noteStr = "[!] " + result
                .split("<tr id=\"Shinweis\">".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
                    .split("</tr>".toRegex())
                    .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
                    .replace("Hinweis: <br />", "")
                    .replace("<br />", "· ")
                .replace("<.*?>".toRegex(), "")
                .replace("&uuml;", "ü")
                .replace("&Uuml;", "Ü")
                .replace("&auml;", "ä")
                .replace("&Auml;", "Ä")
                .replace("&ouml;", "ö")
                .replace("&Ouml;", "Ö")
                .replace("&szlig;", "ß")
                .replace("[\r\n]+".toRegex(), "")
                .trim { it <= ' ' }
        } catch (_: java.lang.Exception) {
        }

        val newC: Array<String>
        try {
             newC = clearUp(result
                        .split("<td class=\"vpTextZentriert\">".toRegex(), limit = 2)
                        .toTypedArray()[1]
                            .split("\n".toRegex())
                            .dropLastWhile { it.isEmpty() }
                        .toTypedArray())
                .split("\n")
                .toTypedArray()
            var counter = 1
            var klasse = ""
            var stunde = ""
            var orgfach = ""
            var vertret = ""
            var raum = ""
            var verfach = ""
            var str: String
            for (cnt in newC) {
                when (counter) {
                    1 -> {
                        klasse = cnt
                        counter++
                    }
                    2 -> {
                        stunde = cnt
                        counter++
                    }
                    3 -> {
                        orgfach = cnt
                        counter++
                    }
                    4 -> {
                        vertret = cnt
                        counter++
                    }
                    5 -> {
                        raum = cnt
                        counter++
                    }
                    6 -> {
                        verfach = cnt
                        counter++
                    }
                    7 -> {
                        str = cnt
                        counter = 1
                        substitutions.add(
                            Substitution(
                                klass = klasse.trim(),
                                lessonNr = stunde.trim(),
                                origSubject = orgfach.trim(),
                                substTeacher = vertret.trim(),
                                substRoom = raum.trim(),
                                substSubject = verfach.trim(),
                                notes = str.trim(),
                                isNew = false
                            )
                        )
                        klasse = ""
                        stunde = ""
                        orgfach = ""
                        vertret = ""
                        raum = ""
                        verfach = ""
                    }
                }
            }
        } catch(ex: Exception) {
            ex.printStackTrace()
            return Result.failure(ex)
        }
        return  if(substitutions.isEmpty()) Result.failure(NoEntriesException())
                else Result.success(SubstitutionSet(
                    date = dateStr,
                    notes = noteStr,
                    substitutions = substitutions
                ))
    }

    private fun clearUp(input: Array<String>): String {
        val me = StringBuilder()
        for (ln2 in input) {
            var ln = ln2
            ln = ln.replace("<.*?>".toRegex(), "")
            ln = ln.replace("&uuml;", "ü")
                .replace("&Uuml;", "Ü")
                .replace("&auml;", "ä")
                .replace("&Auml;", "Ä")
                .replace("&ouml;", "ö")
                .replace("&Ouml;", "Ö")
                .replace("&szlig;", "ß")
            ln = ln.replace("                        ", "")
            ln = ln.trim { it <= ' ' }
            ln = ln.replace("	", "")

            if (ln != "      " &&
                ln != "var hoehe = parent.document.getElementById('inhframe').style.height;" &&
                ln != "setFrameHeight();" &&
                ln != "var pageTracker = _gat._getTracker(\"UA-5496889-1\");" &&
                ln != "pageTracker._trackPageview();" &&
                ln != "    " &&
                ln != "	" &&
                ln != "  " &&
                !ln.startsWith("var") &&
                !ln.startsWith("document.write") &&
                ln != "")

                if (ln.matches(".*\\w.*".toRegex()) || ln.contains("##"))
                    me.append(ln).append("\n")
        }
        return me.toString()
    }*/

    override suspend fun loadSubstitutionPlan(): Result<SubstitutionSet> {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("https://www.gymnasium-sonneberg.de/Informationen/vp.php5")

        if(response.status.value !in 200..299) return Result.failure(
            UnexpectedStatusCodeException("Unexpected code $response")
        )

        return try {
            parser.parseSubstitutionTable(response.body())
            //parseResponse(response.body())
            //return parseResponse(response.body!!.string());
        }catch(ex: Exception){
            ex.printStackTrace()
            Result.failure(ex)
            /*try {
                fallbackLoad(response.body())
            }catch(ex2: Exception) {
                ex2.printStackTrace()
                Result.failure(ex);
            }*/
        }
    }

    /*private suspend fun parseTeachersNumPages(html: String): Int {
        return htmlDocument(html) {
            findFirst("table[class=\"eAusgeben\"] > tbody > tr:last-child > td > a:nth-last-child(2)") {
                this
                    .attribute("href")
                    .substringAfter("seite=")
                    .toInt()
            }
        }
    }*/

    /*private suspend fun parseTeachers(html: String, list: MutableList<Teacher>) {
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
    }*/

    override suspend fun loadTeachers(): Result<List<Teacher>> {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get(
            urlString = "https://www.gymnasium-sonneberg.de/Kontakt/Sprech/ausgeben.php5?seite=1"
        )

        if(response.status.value !in 200..299)
            return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))

        val teachers = ArrayList<Teacher>()
        parser.parseTeachers(response.body(), teachers)

        for (page: Int in 2..parser.parseTeachersNumPages(response.body())) {
            val nextResponse: HttpResponse = client.get(
                urlString = "https://www.gymnasium-sonneberg.de/Kontakt/Sprech/ausgeben.php5?seite="
                        + page.toString()
            )

            if(nextResponse.status.value !in 200..299)
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $nextResponse"))

            parser.parseTeachers(nextResponse.body(), teachers)
        }

        return Result.success(teachers)
    }

    override suspend fun loadSubjects(): Result<List<Subject>> {
        return Result.success(
            listOf(
                Subject("De", "Deutsch", Color(0xFF2196F3)),
                Subject("Ma", "Mathe", Color(0xFFF44336)),
                Subject("Mu", "Musik", Color(0xFF9E9E9E)),
                Subject("Ku", "Kunst", Color(0xFF673AB7)),
                Subject("Gg", "Geografie", Color(0xFF9E9D24)),
                Subject("Re", "Religion", Color(0xFFFF8F00)),
                Subject("Et", "Ethik", Color(0xFFFF8F00)),
                Subject("MNT", "MNT", Color(0xFF4CAF50)),
                Subject("En", "Englisch", Color(0xFFFF9800)),
                Subject("Sp", "Sport", Color(0xFF607D8B)),
                Subject("SpJ", "Sport Jungen", Color(0xFF607D8B)),
                Subject("SpM", "Sport Mädchen", Color(0xFF607D8B)),
                Subject("Bi", "Biologie", Color(0xFF4CAF50)),
                Subject("Ch", "Chemie", Color(0xFFE91E63)),
                Subject("Ph", "Physik", Color(0xFF009688)),
                Subject("Sk", "Sozialkunde", Color(0xFF795548)),
                Subject("If", "Informatik", Color(0xFF03A9F4)),
                Subject("WR", "Wirtschaft/Recht", Color(0xFFFF5722)),
                Subject("Ge", "Geschichte", Color(0xFF9C27B0)),
                Subject("Fr", "Französisch", Color(0xFF558B2F)),
                Subject("Ru", "Russisch", Color(0xFF558B2F)),
                Subject("La", "Latein", Color(0xFF558B2F)),
                Subject("Gewi", "Gesellschaftsw.", Color(0xFF795548)),
                Subject("Dg", "Darstellen/Gestalten", Color(0xFF795548)),
                Subject("Sn", "Spanisch", Color(0xFF558B2F)),
                Subject("&nbsp;", "keine Angabe", Color.DarkGray)
            )
        )
    }

    override suspend fun loadFoodPlan(): Result<List<FoodOffer>> {
        try {
            val client = HttpClient(CIO)
            val response: HttpResponse = client.get("https://schulkueche-bestellung.de/de/menu/14")

            if(response.status.value !in 200..299) return Result.failure(
                UnexpectedStatusCodeException("Unexpected code $response")
            )

            return parser.parseFoodOffers(response.body())
        } catch (e: Exception) {
            //Log.e(TAG, "Konnte Serverantwort nicht verarbeiten: ${e.message}")
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    override suspend fun loadAdditives(): Result<List<Additive>> {
        TODO("Not yet implemented")
    }

}

