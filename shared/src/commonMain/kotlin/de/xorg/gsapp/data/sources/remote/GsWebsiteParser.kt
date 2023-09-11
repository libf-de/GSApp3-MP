package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher

expect class GsWebsiteParser() {
    suspend fun parseSubstitutionTable(result: String): Result<SubstitutionSet>

    suspend fun parseTeachersNumPages(html: String): Int

    suspend fun parseTeachers(html: String, list: MutableList<Teacher>)

    suspend fun parseFoodOffers(html: String): Result<List<FoodOffer>>

}