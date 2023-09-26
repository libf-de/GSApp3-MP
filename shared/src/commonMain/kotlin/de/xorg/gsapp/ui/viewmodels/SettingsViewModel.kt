package de.xorg.gsapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    //private val _teacherState: MutableStateFlow<UiState> = MutableStateFlow(UiState.EMPTY)
    //var teacherState = _teacherState.asStateFlow()
    var teacherState by mutableStateOf(UiState.EMPTY)
        private set

    private val _teacherError: MutableStateFlow<Throwable> = MutableStateFlow(NoException())
    var teacherError = _teacherError.asStateFlow()


    init {
        loadSettings() //TODO: Have settings loading state!
        loadTeachers()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _rolePreference.value = appRepo.getRole()
            _filterPreference.value = appRepo.getFilterValue()
            _pushPreference.value = appRepo.getPush()
        }

    }

    private fun loadTeachers() {
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
    }

    fun setPush(state: PushState) {
        viewModelScope.launch {
            appRepo.setPush(state)
            if(state == PushState.ENABLED || state == PushState.LIKE_FILTER) {
                pushUtil.enablePushService { if(it) _pushPreference.value = state }
            }
        }
    }

    fun setRoleAndFilter(role: FilterRole, filter: String) {
        _rolePreference.value = role
        _filterPreference.value = filter
        viewModelScope.launch {
            appRepo.setRole(role)
            appRepo.setFilterValue(filter)
        }
    }

}