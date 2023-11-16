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
import de.xorg.gsapp.data.model.ComponentData
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.AppState
import de.xorg.gsapp.ui.state.ComponentState
import de.xorg.gsapp.ui.state.UiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.KClass
import kotlin.reflect.KSuspendFunction1

/**
 * View model for the main app tabs
 */
open class GSAppViewModel : ViewModel(), KoinComponent {
    protected val appRepo: GSAppRepository by inject()
    protected val prefsRepo: PreferencesRepository by inject()
    protected val repoScope = CoroutineScope(Dispatchers.IO)

    private var _jobMap = mutableMapOf<String, Job>()

    var uiState by mutableStateOf(AppState())
        private set

    init {
        Napier.d { "GSAppViewModel init" }
    }

    protected fun <T> initState(inputFlow: Flow<T>, targetState: MutableStateFlow<ComponentState<T, Throwable>>) {
        viewModelScope.launch {
            inputFlow
                .mapLatest {
                    if (it.isEmpty()) {
                        ComponentState.Empty
                    } else {
                        ComponentState.Normal(it)
                    }
                }
                .catch {
                    if(it is NoLocalDataException) {
                        targetState.value = ComponentState.EmptyLocal
                        return@catch
                    }
                    val currentState = targetState.value

                    targetState.value = if(currentState is ComponentState.Refreshing)
                        ComponentState.RefreshingFailed(currentState.data, it)
                    else ComponentState.Failed(it)
                }
                .collect {
                    targetState.value = it
                }
        }
    }

    protected fun <T> refresh(
        refreshFunction: suspend ((Result<Boolean>) -> Unit) -> Unit,
        targetState: MutableStateFlow<ComponentState<T, Throwable>>,
        flowToRecoverFrom: Flow<T>
    ) {
        val className = targetState.value::class.simpleName ?: "generic"
        if(_jobMap.containsKey(className)) _jobMap[className]?.cancel()
        if(_jobMap.containsKey("${className}Ref")) _jobMap["${className}Ref"]?.cancel()

        _jobMap[className] = viewModelScope.launch {
            val pastState = targetState.value

            // Enter the refreshing state when there is data to be displayed
            targetState.value = when(pastState) {
                is ComponentState.StateWithData -> ComponentState.Refreshing(pastState.data)
                else -> ComponentState.Loading
            }

            refreshFunction {
                it.onFailure { exception ->
                    targetState.value = ComponentState.Failed(exception)
                }.onSuccess { haveNewData ->
                    /* If there is no new data, ensure we don't stay in a refreshing state, but
                     * revert to the NormalState with the existing data. */
                    if (!haveNewData) targetState.value = when (pastState) {
                        is ComponentState.Refreshing -> ComponentState.Normal(pastState.data)
                        is ComponentState.RefreshingFailed<T, *> -> ComponentState.Normal(pastState.data)
                        else -> pastState
                    }

                    // If there is new data, the state will be updated by the flow.
                }

                /* Ensure that the app does not get stuck in a loading state, and also
                 * make sure this check get's cancelled when the user presses reload (again). */
                _jobMap["${className}Ref"] = viewModelScope.launch {
                    delay(5000L)

                    // Ensure we don't stay in a loading state for more than 5 seconds
                    // after we've received a response from the server
                    if (targetState.value is ComponentState.Loading ||
                        targetState.value is ComponentState.Refreshing ||
                        targetState.value is ComponentState.RefreshingFailed<T, *>
                    ) {
                        targetState.value = flowToRecoverFrom
                            .lastOrNull()?.let { localPlan ->
                                if (localPlan.isEmpty())
                                    ComponentState.Empty
                                else
                                    ComponentState.Normal(localPlan)
                            } ?: ComponentState.EmptyLocal
                    }
                }
            }
        }
    }

    /*private fun initStateFromFlows() {
        viewModelScope.launch {
            subFlow.collect {
                uiState = if (it.isFailure) {
                    if (it.exceptionOrNull() is NoLocalDataException) {
                        if (uiState.substitutionState == UiState.LOADING) {
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
                    if (it.getOrNull()!!.haveUnknownSubs)
                        updateSubjects()

                    if (it.getOrNull()!!.haveUnknownTeachers)
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
                    if (it.exceptionOrNull() is NoLocalDataException) {
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
                uiState = if (it.isFailure) {
                    if (it.exceptionOrNull() is NoLocalDataException) {
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
                    if (it.getOrNull()!!.isEmpty()) {
                        uiState.copy(examState = UiState.EMPTY)
                    } else {
                        uiState.copy(examState = UiState.NORMAL)
                    }
                }
            }
        }
    }*/

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






    /*{
        uiState = if (uiState.foodplanState == UiState.NORMAL)
            uiState.copy(foodplanState = UiState.NORMAL_LOADING)
        else uiState.copy(foodplanState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateFoodplan {
                if (it.isFailure) {
                    Napier.w { "failed to update foodplan: ${it.exceptionOrNull()}" }
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
                    if (it.getOrNull() == false) {
                        //TODO: Notify user of "no new data available"
                        uiState = uiState.copy(foodplanState = UiState.NORMAL)
                        Napier.d { "No new data available (foodplan)!" }
                    } else {
                        Napier.d { "New food data available!" }
                    }
                }
            }
        }
    }*/

    /*fun updateExams() {
        uiState = if (uiState.examState == UiState.NORMAL)
            uiState.copy(examState = UiState.NORMAL_LOADING)
        else uiState.copy(examState = UiState.LOADING)

        repoScope.launch {
            appRepo.updateExams {
                if (it.isFailure) {
                    Napier.w { "failed to update exams: ${it.exceptionOrNull()}" }
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
                    if (it.getOrNull() == false) {
                        //TODO: Notify user of "no new data available"
                        uiState = uiState.copy(examState = UiState.NORMAL)
                        Napier.d { "No new data available (exam)!" }
                    } else {
                        Napier.d { "New exam data available!" }
                    }
                }
            }
        }
    }*/


}

private fun <MapOrList> MapOrList.isEmpty(): Boolean {
    return when(this) {
        is Map<*,*> -> this.isEmpty()
        is List<*> -> this.isEmpty()
        else -> false
    }
}
