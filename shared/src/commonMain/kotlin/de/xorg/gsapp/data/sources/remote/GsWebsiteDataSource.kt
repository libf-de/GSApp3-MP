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
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.datetime.LocalDate


class GsWebsiteDataSource : RemoteDataSource {
    private val parser = GsWebsiteParser()
    private val client = HttpClient()

    override suspend fun loadSubstitutionPlan(): Result<SubstitutionApiModelSet> {
        try {
            val response: HttpResponse =
                client.get("https://www.gymnasium-sonneberg.de/Informationen/vp.php5")
            if (response.status.value !in 200..299) return Result.failure(
                UnexpectedStatusCodeException("Unexpected code $response")
            )

            return try {
                parser.parseSubstitutionTable(response.body())
            } catch (ex: Exception) {
                ex.printStackTrace()
                Result.failure(ex)
            }
        } catch(ex2: Exception) {
            println("Outer ex2 occurred in loadSubstitutionPlan")
            ex2.printStackTrace()
            return Result.failure(ex2)
        }
    }

    override suspend fun loadTeachers(): Result<List<Teacher>> {
        try {
            val response: HttpResponse = client.get(
                urlString = "https://www.gymnasium-sonneberg.de/Kontakt/Sprech/ausgeben.php5?seite=1"
            )

            if (response.status.value !in 200..299)
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))

            val teachers = ArrayList<Teacher>()
            parser.parseTeachers(response.body(), teachers)

            for (page: Int in 2..parser.parseTeachersNumPages(response.body())) {
                val nextResponse: HttpResponse = client.get(
                    urlString = "https://www.gymnasium-sonneberg.de/Kontakt/Sprech/ausgeben.php5?seite="
                            + page.toString()
                )

                if (nextResponse.status.value !in 200..299)
                    return Result.failure(UnexpectedStatusCodeException("Unexpected code $nextResponse"))

                parser.parseTeachers(nextResponse.body(), teachers)
            }

            return Result.success(teachers)
        } catch(ex: Exception) {
            return Result.failure(ex)
        }
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

    override suspend fun loadFoodPlan(): Result<Map<LocalDate, List<Food>>> {
        try {
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

