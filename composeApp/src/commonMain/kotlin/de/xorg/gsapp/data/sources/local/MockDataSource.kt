package de.xorg.gsapp.data.sources.local

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.SubstitutionType
import de.xorg.gsapp.data.model.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

class MockDataSource : LocalDataSource {
    override fun getSubstitutionPlanFlow(): Flow<SubstitutionSet> = flow {
        emit(
            SubstitutionSet(
                dateStr = "24. Dezember 2024",
                date = LocalDate(2024, 12, 24),
                notes = "",
                substitutions = mapOf(
                    "5.1" to listOf(
                        Substitution(
                            type = SubstitutionType.NORMAL,
                            klass = "5.1",
                            klassFilter = "5.1",
                            lessonNr = "1",
                            origSubject = Subject("Deutsch", "De", Color.Blue),
                            substTeacher = Teacher("Herr Müller", "Mü"),
                            substRoom = "A 101",
                            substSubject = Subject("Englisch", "En", Color.Green),
                            notes = "Vertretung",
                            isNew = false
                        ),
                        Substitution(
                            type = SubstitutionType.CANCELLATION,
                            klass = "5.1",
                            klassFilter = "5.1",
                            lessonNr = "2",
                            origSubject = Subject("Mathe", "Ma", Color.Red),
                            substTeacher = Teacher("Frau Schmidt", "Sc"),
                            substRoom = "A 102",
                            substSubject = Subject("Englisch", "En", Color.Green),
                            notes = "Ausfall",
                            isNew = false
                        )
                    )
                )

            )
        )
    }

    override fun getLatestSubstitutionHashAndDate(): Result<Pair<Int, LocalDate>> {
        return Result.success(Pair(0, LocalDate(2024, 12, 24)))
        TODO("Not yet implemented")
    }

    override fun findSubstitutionSetIdByDateString(dateStr: String): Result<Long?> {
        return Result.success(0)
        TODO("Not yet implemented")
    }

    override suspend fun addSubstitutionPlanAndCleanup(value: SubstitutionApiModelSet) {

    }

    override suspend fun addSubstitutionPlan(value: SubstitutionApiModelSet) {

    }

    override suspend fun cleanupSubstitutionPlan() {

    }

    override fun getFoodMapFlow(): Flow<Map<LocalDate, List<Food>>> = flow {
        emit(
            mapOf(
                LocalDate(2024, 12, 24) to listOf(
                    Food(
                        num = 1,
                        name = "Kartoffelsalat",
                        additives = listOf("a")
                    ),
                    Food(
                        num = 2,
                        name = "Wurstsalat",
                        additives = listOf("b")
                    ),
                )
            )
        )
    }

    override fun getFoodsForDateFlow(date: LocalDate): Flow<Result<List<Food>>> = flow {
        emit(
            Result.success(
                listOf(
                    Food(
                        num = 1,
                        name = "Kartoffelsalat",
                        additives = listOf("a")
                    ),
                    Food(
                        num = 2,
                        name = "Wurstsalat",
                        additives = listOf("b")
                    ),
                )
            )
        )
    }

    override fun getFoodDatesFlow(): Flow<Result<List<LocalDate>>> = flow {
        emit(
            Result.success(
                listOf(
                    LocalDate(2024, 12, 24)
                )
            )
        )
    }

    override fun getLatestFoods(): Result<List<Food>> = Result.success(
        listOf(
            Food(
                num = 1,
                name = "Kartoffelsalat",
                additives = listOf("a")
            ),
            Food(
                num = 2,
                name = "Wurstsalat",
                additives = listOf("b")
            ),
        )
    )

    override fun getLatestFoodDate(): Result<LocalDate> = Result.success(LocalDate(2024, 12, 24))

    override suspend fun addFoodMap(value: Map<LocalDate, List<Food>>) { }

    override suspend fun cleanupFoodPlan() { }

    override fun getAdditivesFlow(): Flow<Map<String, String>> = flow {
        emit(
            mapOf(
                "a" to "Konservierungsstoffe",
                "b" to "Farbstoffe"
            )
        )
    }

    override suspend fun addAllAdditives(value: Map<String, String>) { }

    override fun getExamsFlow(): Flow<List<Exam>> = flow {
        emit(
            listOf(
                Exam(
                    label = "EN2",
                    date = LocalDate(2024, 12, 24),
                    course = ExamCourse.COURSE_11,
                    isCoursework = false,
                    subject = Subject("Englisch", "En", Color.Green)
                )
            )
        )
    }

    override suspend fun getAllExams(): Result<List<Exam>> = Result.success(
        listOf(
            Exam(
                label = "EN2",
                date = LocalDate(2024, 12, 24),
                course = ExamCourse.COURSE_11,
                isCoursework = false,
                subject = Subject("Englisch", "En", Color.Green)
            )
        )
    )

    override suspend fun addAllExams(value: List<Exam>) {}

    override suspend fun clearAndAddAllExams(value: List<Exam>) { }

    override suspend fun cleanupExams() {}

    override suspend fun deleteExam(toDelete: Exam) {}

    override fun getSubjectsFlow(): Flow<Result<List<Subject>>> = flow {
        emit(
            Result.success(
                listOf(
                    Subject("Deutsch", "De", Color.Blue),
                    Subject("Mathe", "Ma", Color.Red),
                    Subject("Englisch", "En", Color.Green)
                )
            )
        )
    }

    override suspend fun addAllSubjects(value: List<Subject>) { }

    override suspend fun addSubject(value: Subject) {}

    override suspend fun updateSubject(value: Subject) {}

    override suspend fun deleteSubject(value: Subject) {}

    override suspend fun resetSubjects(value: List<Subject>) {}

    override suspend fun subjectExists(shortName: String): Boolean = listOf("De", "Ma", "En").contains(shortName)

    override suspend fun countSubjects(): Result<Long> = Result.success(3)

    override suspend fun getAllTeachersShorts(): Result<List<String>> = Result.success(listOf("Mü", "Sc"))

    override fun getTeachersFlow(): Flow<Result<List<Teacher>>> = flow {
        emit(
            Result.success(
                listOf(
                    Teacher("Herr Müller", "Mü"),
                    Teacher("Frau Schmidt", "Sc")
                )
            )
        )
    }

    override suspend fun addAllTeachers(value: List<Teacher>) {}

    override suspend fun addTeacher(value: Teacher) { }

    override suspend fun updateTeacher(value: Teacher) { }

    override suspend fun deleteTeacher(value: Teacher) { }
}