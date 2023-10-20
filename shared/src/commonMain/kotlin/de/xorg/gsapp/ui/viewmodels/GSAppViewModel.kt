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

package de.xorg.gsapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.SettingsListener
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
//import com.hoc081098.kmp.viewmodel.ViewModel
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.ui.state.AppState
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.kodein.di.DI
import org.kodein.di.instance
import org.lighthousegames.logging.logging

/**
 * View model for the main app tabs
 */
class GSAppViewModel(di: DI) : ViewModel() {

    companion object {
        val log = logging()
    }

    private val appRepo: GSAppRepository by di.instance()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    var uiState by mutableStateOf(AppState())
        private set

    val subFlow = appRepo.getFilteredSubstitutions().shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
    //private val _subStateFlow = MutableStateFlow(SubstitutionSet())
    //val subStateFlow = _subStateFlow.asStateFlow()

    val foodFlow = appRepo.getFoodplan().shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
    //private val _foodStateFlow = MutableStateFlow(emptyMap<LocalDate, List<Food>>())
    //val foodStateFlow = _foodStateFlow.asStateFlow()

    val examFlow = appRepo.getExams().shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
    //private val _examStateFlow = MutableStateFlow(emptyMap<LocalDate, List<Exam>>())
    //val examStateFlow = _examStateFlow.asStateFlow()

    // Strong references for settings observers. Android Studio marks these as unused,
    // but it's necessary for the Listeners to work.
    // See last paragraph: https://github.com/russhwolf/multiplatform-settings#listeners
    private val _roleObserver = MutableStateFlow<SettingsListener?>(null)
    val roleObserver = _roleObserver.asStateFlow()

    private val _filterObserver = MutableStateFlow<SettingsListener?>(null)
    val filterObserver = _filterObserver.asStateFlow()

    init {
        initStateFromFlows()

        //TODO: Is it a good idea to load all tabs upon app creation?
        //loadSubstitutions()
        //loadFoodplan()
        //loadExams()
        updateExams()
        updateFoodplan()
        updateExams()
    }

    private fun initStateFromFlows() {
        viewModelScope.launch {
            subFlow.collect {
                uiState = if (it.isFailure) {
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
                } else {
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
                    if(uiState.examState == UiState.NORMAL_LOADING ||
                        uiState.examState == UiState.NORMAL) {

                        uiState.copy(examState = UiState.NORMAL_FAILED,
                            examError = it.exceptionOrNull()!!)

                    } else {
                        uiState.copy(examState = UiState.FAILED,
                            examError = it.exceptionOrNull()!!)
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

    private fun updateSubstitutions() {
        uiState = if(uiState.substitutionState == UiState.NORMAL)
            uiState.copy(substitutionState = UiState.NORMAL_LOADING)
        else uiState.copy(substitutionState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateSubstitutions {
                if(it.isFailure) {
                    log.w { "failed to update substitution plan: ${it.exceptionOrNull()}"}
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
                        log.d { "No new data available (substitution plan)!" }
                    } else {
                        log.d { "New substitution data available!" }
                    }
                }
            }
        }
    }

    private fun updateFoodplan() {
        uiState = if(uiState.foodplanState == UiState.NORMAL)
            uiState.copy(foodplanState = UiState.NORMAL_LOADING)
        else uiState.copy(foodplanState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateFoodplan {
                if(it.isFailure) {
                    log.w { "failed to update foodplan: ${it.exceptionOrNull()}"}
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
                        log.d { "No new data available (foodplan)!" }
                    } else {
                        log.d { "New food data available!" }
                    }
                }
            }
        }
    }

    private fun updateExams() {
        uiState = if(uiState.examState == UiState.NORMAL)
            uiState.copy(examState = UiState.NORMAL_LOADING)
        else uiState.copy(examState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateExams {
                if(it.isFailure) {
                    log.w { "failed to update exams: ${it.exceptionOrNull()}"}
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
                        log.d { "No new data available (exam)!" }
                    } else {
                        log.d { "New exam data available!" }
                    }
                }
            }
        }
    }



    /*private suspend fun loadSettings() {
        uiState = uiState.copy(
            filterRole = appRepo.getRole(),
            filter = appRepo.getFilterValue()
        )

        _roleObserver.value = appRepo.observeRole {
            uiState = uiState.copy(filterRole = it)
        } // Should basically not be necessary

        _filterObserver.value = appRepo.observeFilterValue {
            if(it == uiState.filter) return@observeFilterValue
            uiState = uiState.copy(filter = it)
            loadSubstitutions()
        }
    }

    // This is here if I decide to implement pull-to-refresh (or a refresh button).
    fun reloadSubstitutions() {
        loadSubstitutions(true)
    }

    private fun loadExams(reload: Boolean = false) {
        uiState = if(reload) uiState.copy(examReloading = true)
        else uiState.copy(examState = UiState.LOADING)

        viewModelScope.launch {
            val twoDigitYear = Clock.System.todayIn(TimeZone.currentSystemDefault())
                .year.toString().substring(2)
            val defaultCourse = if(uiState.filterRole == FilterRole.STUDENT &&
                uiState.filter == "A${twoDigitYear}") ExamCourse.COURSE_12
            else ExamCourse.COURSE_11

            appRepo.getExams(defaultCourse, reload).collect { examResult ->
                if(examResult.isFailure) {
                    uiState = if(reload) uiState.copy(examReloading = false,
                        examError = examResult.exceptionOrNull()!!)
                    else uiState.copy(examState = UiState.FAILED,
                        examError = examResult.exceptionOrNull()!!)
                    return@collect
                }

                _examStateFlow.value = examResult.getOrNull()!!

                uiState = uiState.copy(
                    examState = if(examResult.getOrNull()!!.isEmpty())
                        UiState.EMPTY
                    else
                        UiState.NORMAL,
                    examReloading = false
                )
            }
        }
    }

    private fun loadSubstitutions(reload: Boolean = false) {
        uiState = if(reload) uiState.copy(substitutionReloading = true)
                  else uiState.copy(substitutionState = UiState.LOADING)

        viewModelScope.launch {
            loadSettings()

            appRepo.getSubstitutions(reload).collect { sdsResult ->
                if(sdsResult.isFailure) {
                    uiState = if(reload) uiState.copy(substitutionReloading = false,
                                                      substitutionError = sdsResult.exceptionOrNull()!!)
                              else uiState.copy(substitutionState = UiState.FAILED,
                                                substitutionError = sdsResult.exceptionOrNull()!!)
                    return@collect
                }

                val substitutions = when (uiState.filterRole) {
                    FilterRole.STUDENT -> {
                        sdsResult.getOrNull()!!.substitutions.filterKeys { entry ->
                            entry.lowercase().contains(uiState.filter.lowercase())
                        }
                    }

                    FilterRole.TEACHER -> {
                        sdsResult.getOrNull()!!.substitutions.mapValues { klassSubs ->
                            klassSubs.value.filter { aSub ->
                                aSub.substTeacher.shortName.lowercase() == uiState.filter.lowercase()
                            }
                        }.filter { klassSubs ->
                            klassSubs.value.isNotEmpty()
                        }
                    }

                    else -> {
                        sdsResult.getOrNull()!!.substitutions
                    }
                }

                _subStateFlow.value = sdsResult.getOrNull()!!.copy(
                    substitutions = substitutions
                )

                uiState = uiState.copy(
                    substitutionState = if(substitutions.isEmpty())
                        UiState.EMPTY
                    else
                        UiState.NORMAL,
                    substitutionReloading = false
                )
            }
        }
    }

    private fun loadFoodplan() {
        uiState = uiState.copy(
            foodplanState = UiState.LOADING
        )

        viewModelScope.launch {
            appRepo.foodPlan.collect {fpResult ->
                if(fpResult.isFailure) {
                    uiState = uiState.copy(
                        foodplanState = UiState.FAILED,
                        foodplanError = fpResult.exceptionOrNull()!!
                    )
                    return@collect
                }

                val foodplan = fpResult.getOrNull()!!

                uiState = uiState.copy(
                    foodplanState = if(foodplan.isEmpty()) UiState.EMPTY else UiState.NORMAL
                )

                _foodStateFlow.value = foodplan
            }
        }
    }*/
}