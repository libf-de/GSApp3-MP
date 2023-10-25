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
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.ColorPickerMode
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.kodein.di.DI
import org.kodein.di.instance

class SettingsViewModel(
    di: DI
) : ViewModel() {
    private val dataRepo: GSAppRepository by di.instance()
    private val prefRepo: PreferencesRepository by di.instance()

    private val pushUtil: PushNotificationUtil by di.instance()

    //TODO: Should I use stateIn here?
    val roleFlow = prefRepo.getRoleFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    val filterFlow = prefRepo.getRoleFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    val pushFlow = prefRepo.getPushFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    /*private val _rolePreference: MutableStateFlow<FilterRole> = MutableStateFlow(FilterRole.ALL)
    var rolePreference = _rolePreference.asStateFlow()

    private val _filterPreference: MutableStateFlow<String> = MutableStateFlow("")
    var filterPreference = _filterPreference.asStateFlow()

    private val _pushPreference: MutableStateFlow<PushState> = MutableStateFlow(PushState.DISABLED)
    var pushPreference = _pushPreference.asStateFlow()*/


    /*private val _teachers: MutableStateFlow<List<Teacher>> = MutableStateFlow(emptyList())
    var teachers = _teachers.asStateFlow()*/
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
    /*private val _subjects: MutableStateFlow<List<Subject>> = MutableStateFlow(emptyList())
    var subjects = _subjects.asStateFlow()*/
    var subjectsState by mutableStateOf(UiState.LOADING)
        private set
    private val _subjectsError: MutableStateFlow<Throwable> = MutableStateFlow(NoException())
    var subjectsError = _subjectsError.asStateFlow()

    private val _colorpickerMode: MutableStateFlow<ColorPickerMode> = MutableStateFlow(ColorPickerMode.default)
    var colorpickerMode = _colorpickerMode.asStateFlow()



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

    /*private fun loadSettings() {
        viewModelScope.launch {
            _rolePreference.value = dataRepo.getRole()
            _filterPreference.value = dataRepo.getFilterValue()
            _pushPreference.value = dataRepo.getPush()
        }
    }*/

    /*private fun loadSubjects() {
        subjectsState = UiState.LOADING

        viewModelScope.launch {
            appRepo.collect { subjectResult ->
                if(subjectResult.isFailure) {
                    subjectsState = UiState.FAILED
                    _subjectsError.value = subjectResult.exceptionOrNull()!!
                    return@collect
                }

                val subjectsList: List<Subject> = subjectResult.getOrNull()!!
                if(subjectsList.isEmpty()) {
                    subjectsState = UiState.EMPTY
                    return@collect
                }

                subjectsState = UiState.NORMAL
                _subjects.value = subjectsList
            }
        }
    }*/

    /*private fun loadTeachers() {
        teacherState = UiState.LOADING
        //_teacherState.value = UiState.LOADING

        viewModelScope.launch {
            appRepo.teachers.collect { teacherResult ->
                if(teacherResult.isFailure) {
                    teacherState = UiState.FAILED
                    //_teacherState.value = UiState.FAILED
                    _teacherError.value = teacherResult.exceptionOrNull()!!
                    return@collect
                }

                val teacherList: List<Teacher> = teacherResult.getOrNull()!!
                if(teacherList.isEmpty()) {
                    teacherState = UiState.EMPTY
                    //_teacherState.value = UiState.EMPTY
                    return@collect
                }

                teacherState = UiState.NORMAL
                //_teacherState.value = UiState.NORMAL
                _teachers.value = teacherList
            }
        }
    }*/

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
                    pushUtil.enablePushService {
                        viewModelScope.launch {
                            prefRepo.setPush(state)
                        }
                    }
                }
                //TODO: Notify user of missing permissions!
            }
        } else {
            pushUtil.disablePushService {
                viewModelScope.launch {
                    prefRepo.setPush(state)
                }
            }
        }
    }

    fun setRoleAndFilter(role: FilterRole, filter: String) {
        viewModelScope.launch {
            prefRepo.setRole(role)
            prefRepo.setFilterValue(filter)
        }
    }

    fun setColorpickerMode(pickerMode: ColorPickerMode) {
        _colorpickerMode.value = pickerMode
    }

}