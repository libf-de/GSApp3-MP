package de.xorg.gsapp.data.sources.remote

import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.datetime.LocalDate
import org.kodein.di.DI

interface RemoteDataSource {
    suspend fun getSubstitutionPlan(): Result<SubstitutionApiModelSet>
    suspend fun getTeachers(): Result<List<Teacher>>
    suspend fun getFoodplan(): Result<Map<LocalDate, List<Food>>>
    suspend fun getAdditives(): Result<Map<String, String>>
    suspend fun getFoodplanAndAdditives(): Result<Pair<Map<LocalDate, List<Food>>, Map<String, String>>>
    suspend fun getExams(): Result<List<Exam>>
}