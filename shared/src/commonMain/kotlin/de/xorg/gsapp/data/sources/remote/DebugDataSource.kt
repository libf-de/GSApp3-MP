package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.exceptions.UnexpectedStatusCodeException
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class DebugDataSource : WebsiteDataSource() {
    override suspend fun getSubstitutionPlan(): Result<SubstitutionApiModelSet> {
        try {
            val response: HttpResponse =
                client.get("http://agdsn.me/~xorg/gsapp-old/debug/vp.html")
            if (response.status.value !in 200..299) {
                log.e { "loadSubstitutionPlan(): Unexpected code: $response" }
                return Result.failure(UnexpectedStatusCodeException("Unexpected code $response"))
            }

            log.d { "got substitution plan" }

            return try {
                parser.parseSubstitutionTable(response.body())
            } catch (ex: Exception) {
                log.e { "inner ex: ${ex.stackTraceToString()}" }
                Result.failure(ex)
            }
        } catch(ex2: Exception) {
            log.e { "outer ex2: ${ex2.stackTraceToString()}" }
            return Result.failure(ex2)
        }
    }
}