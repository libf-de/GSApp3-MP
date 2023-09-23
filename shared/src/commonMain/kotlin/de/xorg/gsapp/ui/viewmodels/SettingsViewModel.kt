package de.xorg.gsapp.ui.viewmodels

import de.xorg.gsapp.data.exceptions.NoException
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.kodein.di.DI
import org.kodein.di.instance

class SettingsViewModel(
    di: DI
) : ViewModel() {
    private val appRepo: GSAppRepository by di.instance()

    private val _rolePreference: MutableStateFlow<FilterRole> = MutableStateFlow(FilterRole.ALL)
    var rolePreference = _rolePreference.asStateFlow()

    private val _filterPreference: MutableStateFlow<String> = MutableStateFlow("")
    var filterPreference = _filterPreference.asStateFlow()

    private val _pushPreference: MutableStateFlow<PushState> = MutableStateFlow(PushState.DISABLED)
    var pushPreference = _pushPreference.asStateFlow()
    private val pushUtil: PushNotificationUtil by di.instance()

    private val _teachers: MutableStateFlow<List<Teacher>> = MutableStateFlow(emptyList())
    var teachers = _teachers.asStateFlow()
    private val _teacherState: MutableStateFlow<UiState> = MutableStateFlow(UiState.EMPTY)
    var teacherState = _teacherState.asStateFlow()
    private val _teacherError: MutableStateFlow<Throwable> = MutableStateFlow(NoException())
    var teacherError = _teacherError.asStateFlow()


    init {
        loadTeachers()
    }

    private fun loadTeachers() {
        _teacherState.value = UiState.LOADING

        viewModelScope.launch {
            appRepo.teachers.collect { teacherResult ->
                if(teacherResult.isFailure) {
                    _teacherState.value = UiState.FAILED
                    _teacherError.value = teacherResult.exceptionOrNull()!!
                    return@collect
                }

                val teacherList: List<Teacher> = teacherResult.getOrNull()!!
                if(teacherList.isEmpty()) {
                    _teacherState.value = UiState.EMPTY
                    return@collect
                }

                _teacherState.value = UiState.NORMAL
                _teachers.value = teacherList
            }
        }
    }

    fun setPush(state: PushState) {
        viewModelScope.launch {
            appRepo.setPush(state)
            if(state == PushState.ENABLED || state == PushState.LIKE_FILTER) {
                pushUtil.enablePushService { if(it) _pushPreference.value = state }
            }
        }
    }

    fun setRole(role: FilterRole) {
        _rolePreference.value = role
        viewModelScope.launch { appRepo.setRole(role) }
    }

    fun setFilter(value: String) {
        _filterPreference.value = value
        viewModelScope.launch { appRepo.setFilterValue(value) }
    }

}