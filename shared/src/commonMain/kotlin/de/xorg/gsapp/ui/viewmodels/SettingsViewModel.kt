package de.xorg.gsapp.ui.viewmodels

import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
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
    private val _rolePreference: MutableStateFlow<FilterRole> = MutableStateFlow(FilterRole.ALL)
    var rolePreference = _rolePreference.asStateFlow()

    private val _filterPreference: MutableStateFlow<String> = MutableStateFlow("")
    var filterPreference = _filterPreference.asStateFlow()

    private val _pushPreference: MutableStateFlow<PushState> = MutableStateFlow(PushState.DISABLED)
    var pushPreference = _pushPreference.asStateFlow()
    private val pushUtil: PushNotificationUtil by di.instance()

    fun setPush(state: PushState) {
        viewModelScope.launch {
            if(state == PushState.ENABLED || state == PushState.LIKE_FILTER) {
                pushUtil.enablePushService { if(it) _pushPreference.value = state }
            }
        }
    }

    fun setRole(role: FilterRole) { _rolePreference.value = role }

    fun setFilter(value: String) { _filterPreference.value = value }

}