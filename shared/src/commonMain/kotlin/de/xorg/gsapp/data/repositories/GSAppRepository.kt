package de.xorg.gsapp.data.repositories

import com.russhwolf.settings.SettingsListener
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface GSAppRepository {
    suspend fun getSubstitutions(reload: Boolean): Flow<Result<SubstitutionSet>>

    val teachers: Flow<Result<List<Teacher>>>

    val subjects: Flow<Result<List<Subject>>>

    val foodPlan: Flow<Result<Map<LocalDate, List<Food>>>>

    val additives: Flow<Result<Map<String, String>>>

    suspend fun addTeacher(value: Teacher): Result<Boolean>

    suspend fun deleteTeacher(value: Teacher): Result<Boolean>

    suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher>

    suspend fun addSubject(value: Subject): Result<Boolean>

    suspend fun deleteSubject(value: Subject): Result<Boolean>

    suspend fun updateSubject(oldSub: Subject, newSub: Subject): Result<Subject>

    suspend fun getRole(): FilterRole
    suspend fun setRole(value: FilterRole)
    suspend fun observeRole(callback: (FilterRole) -> Unit): SettingsListener?

    suspend fun getFilterValue(): String
    suspend fun setFilterValue(value: String)
    suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener?

    suspend fun getPush(): PushState
    suspend fun setPush(value: PushState)
    suspend fun observePush(callback: (PushState) -> Unit): SettingsListener?
}