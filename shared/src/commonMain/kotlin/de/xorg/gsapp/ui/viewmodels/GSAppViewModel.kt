/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
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

package de.xorg.gsapp.ui.viewmodels

//import com.hoc081098.kmp.viewmodel.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.NoLocalDataException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.AppState
import de.xorg.gsapp.ui.state.UiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * View model for the main app tabs
 */
class GSAppViewModel : ViewModel(), KoinComponent {
    private val appRepo: GSAppRepository by inject()
    private val prefsRepo: PreferencesRepository by inject()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    var uiState by mutableStateOf(AppState())
        private set

    var examState = MutableStateFlow(ExamCourse.COURSE_11)

    val subFlow = combine(appRepo.getSubstitutions(),
                          prefsRepo.getFilterFlow()) { subs, filter ->
        if(filter.role == Filter.Role.ALL) return@combine subs

        subs.mapCatching {
            it.copy(
                substitutions = it.substitutions.mapValues { subsPerClass ->
                    subsPerClass.value.filter { aSub ->
                        filter.substitutionMatches(aSub)
                    }
                }.filter {
                        subsPerClass -> subsPerClass.value.isNotEmpty()
                }
            )
        }
    }.shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )


    val foodFlow = appRepo.getFoodplan().shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )



    val examFlow = combine(
        appRepo.getExams(),
        examState
    ) { exams, course ->
        if(exams.isFailure) return@combine exams

        val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return@combine Result.success(
            exams.getOrNull()!!.filter { exam ->
                exam.course == course && exam.date >= today
            }
        )
    }.shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    init {
        Napier.d { "GSAppViewModel init" }

        initStateFromFlows()

        Napier.d { "updating data..." }
        updateExams()
        updateFoodplan()
        updateSubstitutions()
    }

    private fun initStateFromFlows() {
        viewModelScope.launch {
            subFlow.collect {
                uiState = if (it.isFailure) {
                    if (it.exceptionOrNull() is NoLocalDataException) {
                        if(uiState.substitutionState == UiState.LOADING) {
                            uiState.copy(substitutionState = UiState.LOADING)
                        } else {
                            uiState.copy(substitutionState = UiState.EMPTY_LOCAL)
                        }
                    } else {
                        if (uiState.substitutionState == UiState.NORMAL_LOADING ||
                            uiState.substitutionState == UiState.NORMAL
                        ) {

                            uiState.copy(
                                substitutionState = UiState.NORMAL_FAILED,
                                substitutionError = it.exceptionOrNull()!!
                            )

                        } else {
                            uiState.copy(
                                substitutionState = UiState.FAILED,
                                substitutionError = it.exceptionOrNull()!!
                            )
                        }
                    }
                } else {
                    if(it.getOrNull()!!.haveUnknownSubs)
                        updateSubjects()

                    if(it.getOrNull()!!.haveUnknownTeachers)
                        updateTeachers()


                    if (it.getOrNull()!!.substitutions.isEmpty()) {
                        uiState.copy(substitutionState = UiState.EMPTY)
                    } else {
                        uiState.copy(substitutionState = UiState.NORMAL)
                    }
                }
            }
        }
        viewModelScope.launch {
            foodFlow.collect {
                uiState = if (it.isFailure) {
                    if(it.exceptionOrNull() is NoLocalDataException) {
                        uiState.copy(foodplanState = UiState.EMPTY_LOCAL)
                    } else {
                        if (uiState.foodplanState == UiState.NORMAL_LOADING ||
                            uiState.foodplanState == UiState.NORMAL
                        ) {
                            uiState.copy(
                                foodplanState = UiState.NORMAL_FAILED,
                                foodplanError = it.exceptionOrNull()!!
                            )
                        } else {
                            uiState.copy(
                                foodplanState = UiState.FAILED,
                                foodplanError = it.exceptionOrNull()!!
                            )
                        }
                    }
                } else {
                    if (it.getOrNull()!!.isEmpty()) {
                        uiState.copy(foodplanState = UiState.EMPTY)
                    } else {
                        uiState.copy(foodplanState = UiState.NORMAL)
                    }
                }
            }
        }

        viewModelScope.launch {
            examFlow.collect {
                uiState = if(it.isFailure) {
                    if(it.exceptionOrNull() is NoLocalDataException) {
                        uiState.copy(examState = UiState.EMPTY_LOCAL)
                    } else {
                        if (uiState.examState == UiState.NORMAL_LOADING ||
                            uiState.examState == UiState.NORMAL
                        ) {

                            uiState.copy(
                                examState = UiState.NORMAL_FAILED,
                                examError = it.exceptionOrNull()!!
                            )

                        } else {
                            uiState.copy(
                                examState = UiState.FAILED,
                                examError = it.exceptionOrNull()!!
                            )
                        }
                    }
                } else {
                    if(it.getOrNull()!!.isEmpty()) {
                        uiState.copy(examState = UiState.EMPTY)
                    } else {
                        uiState.copy(examState = UiState.NORMAL)
                    }
                }
            }
        }
    }

    private fun updateSubjects(force: Boolean = false) {
        repoScope.launch {
            appRepo.updateSubjects { }
        }
    }

    private fun updateTeachers() {
        repoScope.launch {
            appRepo.updateTeachers { }
        }
    }

    fun updateSubstitutions() {
        Napier.d { "updating substitutions started" }
        uiState = if(uiState.substitutionState == UiState.NORMAL)
            uiState.copy(substitutionState = UiState.NORMAL_LOADING)
        else uiState.copy(substitutionState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateSubstitutions {
                if(it.isFailure) {
                    Napier.w { "failed to update substitution plan: ${it.exceptionOrNull()}"}
                    uiState = if (uiState.substitutionState == UiState.NORMAL_LOADING ||
                        uiState.substitutionState == UiState.NORMAL
                    ) {
                        uiState.copy(
                            substitutionState = UiState.NORMAL_FAILED,
                            substitutionError = it.exceptionOrNull()!!
                        )
                    } else {
                        uiState.copy(
                            substitutionState = UiState.FAILED,
                            substitutionError = it.exceptionOrNull()!!
                        )
                    }
                } else {
                    if(it.getOrNull() == false) {
                        //TODO: Notify user of "no new data available"
                        uiState = uiState.copy(substitutionState = UiState.NORMAL)
                        Napier.d { "No new data available (substitution plan)!" }
                    } else {
                        Napier.d { "New substitution data available!" }
                    }
                }
            }
        }
    }

    fun updateFoodplan() {
        uiState = if(uiState.foodplanState == UiState.NORMAL)
            uiState.copy(foodplanState = UiState.NORMAL_LOADING)
        else uiState.copy(foodplanState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateFoodplan {
                if(it.isFailure) {
                    Napier.w { "failed to update foodplan: ${it.exceptionOrNull()}"}
                    uiState = if (uiState.foodplanState == UiState.NORMAL_LOADING ||
                        uiState.foodplanState == UiState.NORMAL
                    ) {
                        uiState.copy(
                            foodplanState = UiState.NORMAL_FAILED,
                            foodplanError = it.exceptionOrNull()!!
                        )
                    } else {
                        uiState.copy(
                            foodplanState = UiState.FAILED,
                            foodplanError = it.exceptionOrNull()!!
                        )
                    }
                } else {
                    if(it.getOrNull() == false) {
                        //TODO: Notify user of "no new data available"
                        uiState = uiState.copy(foodplanState = UiState.NORMAL)
                        Napier.d { "No new data available (foodplan)!" }
                    } else {
                        Napier.d { "New food data available!" }
                    }
                }
            }
        }
    }

    fun updateExams() {
        uiState = if(uiState.examState == UiState.NORMAL)
            uiState.copy(examState = UiState.NORMAL_LOADING)
        else uiState.copy(examState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateExams {
                if(it.isFailure) {
                    Napier.w { "failed to update exams: ${it.exceptionOrNull()}"}
                    uiState = if (uiState.examState == UiState.NORMAL_LOADING ||
                        uiState.examState == UiState.NORMAL
                    ) {
                        uiState.copy(
                            examState = UiState.NORMAL_FAILED,
                            examError = it.exceptionOrNull()!!
                        )
                    } else {
                        uiState.copy(
                            examState = UiState.FAILED,
                            examError = it.exceptionOrNull()!!
                        )
                    }
                } else {
                    if(it.getOrNull() == false) {
                        //TODO: Notify user of "no new data available"
                        uiState = uiState.copy(examState = UiState.NORMAL)
                        Napier.d { "No new data available (exam)!" }
                    } else {
                        Napier.d { "New exam data available!" }
                    }
                }
            }
        }
    }

    fun toggleCourse() {
        examState.value = when(examState.value) {
            ExamCourse.COURSE_11 -> ExamCourse.COURSE_12
            ExamCourse.COURSE_12 -> ExamCourse.COURSE_11
        }
    }
}