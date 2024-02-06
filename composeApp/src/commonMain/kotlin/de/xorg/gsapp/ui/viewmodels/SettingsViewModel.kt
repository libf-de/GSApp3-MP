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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.exceptions.NoException
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.ColorPickerMode
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {
    private val dataRepo: GSAppRepository by inject()
    private val prefRepo: PreferencesRepository by inject()

    private val pushUtil: PushNotificationUtil by inject()

    //TODO: Should I use stateIn here?
    /*val roleFlow = prefRepo.getRoleFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    val filterFlow = prefRepo.getFilterValueFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )*/
    val filterFlow = prefRepo.getFilterFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    val pushFlow = prefRepo.getPushFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    val teachers = dataRepo.getTeachers().shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
    private val _teacherError: MutableStateFlow<Throwable> = MutableStateFlow(NoException())
    var teacherError = _teacherError.asStateFlow()
    var teacherState by mutableStateOf(UiState.EMPTY)
        private set

    var subjects = dataRepo.getSubjects().shareIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    var subjectsState by mutableStateOf(UiState.LOADING)
        private set
    private val _subjectsError: MutableStateFlow<Throwable> = MutableStateFlow(NoException())
    var subjectsError = _subjectsError.asStateFlow()

    private val _colorpickerMode: MutableStateFlow<ColorPickerMode> = MutableStateFlow(ColorPickerMode.default)
    var colorpickerMode = _colorpickerMode.asStateFlow()

    private val _firebaseLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var firebaseLoading = _firebaseLoading.asStateFlow()



    init {
        /*viewModelScope.launch {
            appRepo.updateSubjects {  }
        }*/
        //loadSettings() //TODO: Have settings loading state!
        initStateFromFlows()

    }

    private fun initStateFromFlows() {
        viewModelScope.launch {
            subjects.collect {
                //println(it)

                subjectsState = if (it.isFailure) {
                    if (subjectsState == UiState.NORMAL_LOADING ||
                        subjectsState == UiState.NORMAL
                    ) {
                        UiState.NORMAL_FAILED
                    } else {
                        UiState.FAILED
                    }
                } else {
                    if (it.getOrNull()!!.isEmpty()) {
                        UiState.EMPTY
                    } else {
                        UiState.NORMAL
                    }
                }
            }
        }

        viewModelScope.launch {
            teachers.collect {
                teacherState = if (it.isFailure) {
                    if (teacherState == UiState.NORMAL_LOADING ||
                        teacherState == UiState.NORMAL
                    ) {
                        UiState.NORMAL_FAILED
                    } else {
                        UiState.FAILED
                    }
                } else {
                    if (it.getOrNull()!!.isEmpty()) {
                        UiState.EMPTY
                    } else {
                        UiState.NORMAL
                    }
                }
            }
        }
    }

    fun addSubject(subject: Subject) {
        viewModelScope.launch {
            dataRepo.addSubject(subject)
        }
    }

    fun updateSubject(oldSubject: Subject, longName: String? = null, color: Color? = null) {
        viewModelScope.launch {
            dataRepo.editSubject(oldSubject, longName, color)
        }
    }

    fun updateSubjects(force: Boolean = false) {
        viewModelScope.launch {
            dataRepo.updateSubjects(force) { }
        }
    }

    fun resetSubjects() {
        viewModelScope.launch {
            dataRepo.resetSubjects()
        }
    }

    fun deleteSubject(toDelete: Subject) {
        viewModelScope.launch {
            dataRepo.deleteSubject(toDelete)
        }
    }

    // TODO: Handle failure properly (somehow)
    fun setPush(state: PushState) {
        if(state == PushState.ENABLED || state == PushState.LIKE_FILTER) {
            pushUtil.ensurePushPermissions {  success ->
                if(success) {
                    _firebaseLoading.value = true
                    pushUtil.enablePushService {
                        if(it) {
                            viewModelScope.launch {
                                prefRepo.setPush(state)
                                _firebaseLoading.value = false
                            }
                        }
                    }
                }
                //TODO: Notify user of missing permissions!
            }
        } else {
            _firebaseLoading.value = true
            pushUtil.disablePushService {
                viewModelScope.launch {
                    prefRepo.setPush(state)
                    _firebaseLoading.value = false
                }
            }
        }
    }

    fun setFilter(filter: Filter) {
        viewModelScope.launch {
            prefRepo.setFilter(filter)
        }
    }

    fun setColorpickerMode(pickerMode: ColorPickerMode) {
        _colorpickerMode.value = pickerMode
    }

}