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
import de.xorg.gsapp.LAUNCH_VERSION
import de.xorg.gsapp.data.di.JOB_MAP
import de.xorg.gsapp.data.exceptions.AppStuckInLoadingException
import de.xorg.gsapp.data.exceptions.NoLocalDataException
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.AppState
import de.xorg.gsapp.ui.state.ComponentState
import de.xorg.gsapp.ui.tools.JobTool
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

/**
 * View model for the main app tabs
 */
open class GSAppViewModel : ViewModel(), KoinComponent {
    protected val appRepo: GSAppRepository by inject()
    protected val prefsRepo: PreferencesRepository by inject()
    protected val repoScope = CoroutineScope(Dispatchers.IO)

    protected val jobTool: JobTool by inject()

    protected val jobMap: MutableMap<String, Job> by inject(qualifier = named(JOB_MAP))

    var uiState by mutableStateOf(AppState())
        private set

    init {
        Napier.d { "GSAppViewModel init" }

        // Populate database with default values if necessary
        jobTool.singletonIgnoringJob("dbDefaultsInit", viewModelScope) {
            appRepo.handleUpdate(prefsRepo.getLaunchVersion())
            prefsRepo.setLaunchVersion(LAUNCH_VERSION)
        }
    }

    /**
     * Initializes a state flow from a given input flow.
     * Should be used by the subclasses to initialize their state flows.
     * @param inputFlow The input flow
     * @param targetState The state flow to update
     */
    protected fun <T> initState(inputFlow: Flow<T>, targetState: MutableStateFlow<ComponentState<T, Throwable>>) {
        viewModelScope.launch {
            Napier.d { "initState" }
            inputFlow
                .mapLatest {
                    if (it.isEmpty()) {
                        ComponentState.Empty
                    } else {
                        ComponentState.Normal(it)
                    }
                }/* This is a problem - if catch gets active, the flow isn't collected */
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
                    Napier.d { "GSAppViewModel: New state: $it" }
                }
        }
    }

    /**
     * Refreshes a state flow from a given refresh function.
     * Should be used by the subclasses to refresh their state flows.
     * @param refreshFunction The refresh function
     * @param targetState The state flow to update
     * @param flowToRecoverFrom The flow to recover from if the refresh function fails (unused)
     */
    protected fun <T> refresh(
        refreshFunction: suspend ( suspend(Result<Boolean>) -> Unit) -> Unit,
        targetState: MutableStateFlow<ComponentState<T, Throwable>>,
        flowToRecoverFrom: Flow<T>
    ) {
        val className = targetState.value::class.simpleName ?: "generic"

        // Cancel existing "ensure not stuck in refreshing" job
        jobTool.cancelJob("${className}Ref")

        // Replace existing refresh job
        jobTool.singletonReplacingJob(
            jobName = className,
            scope = viewModelScope
        ) {
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
                        is ComponentState.Loading -> ComponentState.EmptyLocal /* Should not happen */
                        is ComponentState.Refreshing -> ComponentState.Normal(pastState.data)
                        is ComponentState.RefreshingFailed<T, *> -> ComponentState.Normal(pastState.data)
                        else -> pastState
                    }

                    // If there is new data, the state will be updated by the flow.
                }

                //TODO: The only case this may happen is when there is new data, but the database
                // doesn't emit it - so in theory should NEVER happen?

                /* Ensure that the app does not get stuck in a loading state, and also
                 * make sure this check get's cancelled when the user presses reload (again). */
                jobTool.singletonReplacingJob(
                    jobName = "${className}Ref",
                    scope = viewModelScope
                ) {
                    // Wait 5 seconds before checking if we're still in a loading state
                    delay(5000L)

                    // Ensure we don't stay in a loading state for more than 5 seconds
                    // after we've received a response from the server
                    if (targetState.value is ComponentState.Loading ||
                        targetState.value is ComponentState.Refreshing ||
                        targetState.value is ComponentState.RefreshingFailed<T, *>
                    ) {
                        Napier.w { "App stuck in a loading state!" }
                        targetState.value = ComponentState.Failed(AppStuckInLoadingException())
                        /*targetState.value = flowToRecoverFrom
                            .lastOrNull()?.let { localPlan ->
                                if (localPlan.isEmpty())
                                    ComponentState.Empty
                                else
                                    ComponentState.Normal(localPlan)
                            } ?: ComponentState.EmptyLocal*/
                    }
                }
            }
        }
    }
}

private fun <MapOrList> MapOrList.isEmpty(): Boolean {
    return when(this) {
        is Map<*,*> -> this.isEmpty()
        is List<*> -> this.isEmpty()
        else -> false
    }
}
