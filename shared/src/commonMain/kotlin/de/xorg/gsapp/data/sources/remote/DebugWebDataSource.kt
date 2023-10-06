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
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.Teacher
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.datetime.LocalDate


class DebugWebDataSource : GsWebsiteDataSource() {
    private val parser = GsWebsiteParser()
    private val client = HttpClient()

    private var foodplanHtmlCache: String? = null

    override suspend fun loadSubstitutionPlan(): Result<SubstitutionApiModelSet> {
        try {
            val response: HttpResponse =
                client.get("https://agdsn.me/~xorg/gsapp/debug/vpl.html")
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
}

