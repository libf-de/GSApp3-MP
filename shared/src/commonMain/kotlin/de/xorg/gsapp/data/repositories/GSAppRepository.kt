package de.xorg.gsapp.data.repositories

import de.xorg.gsapp.data.model.Additive
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GSAppRepository {
    fun getSubstitutions(): Flow<Result<SubstitutionSet>>

    val foodPlan: Flow<Result<Map<LocalDate, List<Food>>>>

    val additives: Flow<Result<List<Additive>>>

    suspend fun addTeacher(value: Teacher): Result<Boolean>

    suspend fun deleteTeacher(value: Teacher): Result<Boolean>

    suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher>

    suspend fun addSubject(value: Subject): Result<Boolean>

    suspend fun deleteSubject(value: Subject): Result<Boolean>

    suspend fun updateSubject(oldSub: Subject, newSub: Subject): Result<Subject>

}