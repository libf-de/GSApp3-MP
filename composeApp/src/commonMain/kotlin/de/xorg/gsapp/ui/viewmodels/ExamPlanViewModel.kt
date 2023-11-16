package de.xorg.gsapp.ui.viewmodels

import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.ui.state.ComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class ExamPlanViewModel : GSAppViewModel() {
    var _courseState = MutableStateFlow(ExamCourse.COURSE_11)
    val courseState: StateFlow<ExamCourse> = _courseState

    private val _examState =
        MutableStateFlow<ComponentState<List<Exam>, Throwable>>(ComponentState.EmptyLocal)
    val examState: StateFlow<ComponentState<List<Exam>, Throwable>> =
        _examState

    private val examFlow = combine(
        appRepo.getExams(),
        courseState
    ) { exams, course ->
        val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        exams.filter { exam ->
            exam.course == course && exam.date >= today
        }
    }

    init {
        initState(examFlow, _examState)
    }

    fun updateExams() = refresh(
        refreshFunction = appRepo::updateExams,
        targetState = _examState,
        flowToRecoverFrom = examFlow,
    )

    fun toggleCourse() {
        _courseState.value = when (_courseState.value) {
            ExamCourse.COURSE_11 -> ExamCourse.COURSE_12
            ExamCourse.COURSE_12 -> ExamCourse.COURSE_11
        }
    }
}