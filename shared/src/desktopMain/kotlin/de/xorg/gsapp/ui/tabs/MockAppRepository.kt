package de.xorg.gsapp.ui.tabs

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.model.Additive
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.repositories.GSAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class MockAppRepository : GSAppRepository {
    override fun getSubstitutions(): Flow<Result<SubstitutionSet>> = flow {
        val subs = mapOf(
            "5.3" to listOf(Substitution(
                type = SubstitutionType.NORMAL,
                klass = "5.3",
                lessonNr = "4",
                origSubject = Subject(
                    shortName = "Ma",
                    longName = "Mathematik",
                    color = Color.Red
                ),
                substTeacher = Teacher(
                    shortName = "MUS", longName = "Mustermann, Max"
                ),
                substRoom = "L01",
                substSubject = Subject(
                    shortName = "If",
                    longName = "Informatik",
                    color = Color.Blue
                ),
                notes = "",
                isNew = false
            ))
        )
        val sds = SubstitutionSet(
            date = "01.01.2000",
            notes = "",
            substitutions = subs
        )
    }

    override val foodPlan: Flow<Result<Map<LocalDate, List<Food>>>>
        get() = TODO("Not yet implemented")
    override val additives: Flow<Result<List<Additive>>>
        get() = TODO("Not yet implemented")

    override suspend fun addTeacher(value: Teacher): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTeacher(value: Teacher): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTeacher(oldTea: Teacher, newTea: Teacher): Result<Teacher> {
        TODO("Not yet implemented")
    }

    override suspend fun addSubject(value: Subject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSubject(value: Subject): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSubject(oldSub: Subject, newSub: Subject): Result<Subject> {
        TODO("Not yet implemented")
    }
}