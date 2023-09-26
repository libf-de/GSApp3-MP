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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
//import com.hoc081098.kmp.viewmodel.ViewModel
import de.xorg.gsapp.data.di.repositoryModule
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.ui.state.AppState
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.ProtoState
import de.xorg.gsapp.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

//TODO: Wenn hier fehler, dann schau was mit dem Parameter ist.
class GSAppViewModel(di: DI) : ViewModel() {
    private val appRepo: GSAppRepository by di.instance()

    var uiState by mutableStateOf(AppState())
        private set

    private val _subStateFlow = MutableStateFlow(SubstitutionSet())
    val subStateFlow = _subStateFlow.asStateFlow()

    private val _foodStateFlow = MutableStateFlow(emptyMap<LocalDate, List<Food>>())
    val foodStateFlow = _foodStateFlow.asStateFlow()

    private val _roleObserver = MutableStateFlow<SettingsListener?>(null)
    val roleObserver = _roleObserver.asStateFlow()

    private val _filterObserver = MutableStateFlow<SettingsListener?>(null)
    val filterObserver = _filterObserver.asStateFlow()

    init {
        loadSubstitutions()
        loadFoodplan()
    }

    private suspend fun loadSettings() {
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

    private fun loadSubstitutions() {
        uiState = uiState.copy(
            substitutionState = UiState.LOADING
        )

        viewModelScope.launch {
            loadSettings()

            appRepo.getSubstitutions().collect { sdsResult ->
                if(sdsResult.isFailure) {
                    //Log.d("GSAppViewModel:loadSubs", "Error while fetching: ${sdsResult.exceptionOrNull()}")
                    uiState = uiState.copy(
                        substitutionState = UiState.FAILED,
                        substitutionError = sdsResult.exceptionOrNull()!!
                    )
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
                    //Log.d("GSAppViewModel:loadSubs", "Error while fetching: ${sdsResult.exceptionOrNull()}")
                    uiState = uiState.copy(
                        foodplanState = UiState.FAILED,
                        foodplanError = fpResult.exceptionOrNull()!!
                    )
                    return@collect
                }

                val foodplan = fpResult.getOrNull()!!
                //Log.d("GSAppViewModel:loadSubs", "Got SubstitutionsDisplaySet " +
                //        "with ${sds.substitutions} substitutions for ${sds.date}")

                uiState = uiState.copy(
                    foodplanState = if(foodplan.isEmpty()) UiState.EMPTY else UiState.NORMAL
                )

                _foodStateFlow.value = foodplan
            }
        }
    }
}