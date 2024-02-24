package de.xorg.gsapp.data.repositories

import com.russhwolf.settings.SettingsListener
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockSettingsRepository : PreferencesRepository {
    override fun getRoleFlow(): Flow<Filter.Role> = flow {
        emit(Filter.Role.ALL)
    }

    override suspend fun getRole(): Filter.Role = Filter.Role.ALL

    override suspend fun setRole(value: Filter.Role) { }

    override suspend fun observeRole(callback: (Filter.Role) -> Unit): SettingsListener? = null

    override fun getFilterValueFlow(): Flow<String> = flow {
        emit("")
    }

    override suspend fun getFilterValue(): String = ""

    override suspend fun setFilterValue(value: String) { }

    override suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener? = null

    override fun getFilterFlow(): Flow<Filter> = flow {
        emit(Filter.NONE)
    }

    override suspend fun getFilter(): Filter = Filter.NONE

    override suspend fun setFilter(value: Filter) { }

    override fun getPushFlow(): Flow<PushState> = flow {
        emit(PushState.DISABLED)
    }

    override suspend fun getPush(): PushState = PushState.DISABLED

    override suspend fun setPush(value: PushState) { }

    override suspend fun observePush(callback: (PushState) -> Unit): SettingsListener? = null

    override fun getAskUserForNotificationPermissionFlow(): Flow<Boolean> = flow {
        emit(false)
    }

    override suspend fun getAskUserForNotificationPermission(): Boolean = false

    override suspend fun setAskUserForNotificationPermission(value: Boolean) { }

    override suspend fun observeAskUserForNotificationPermission(callback: (Boolean) -> Unit): SettingsListener? = null

    override fun getLaunchVersionFlow(): Flow<Int> = flow {
        emit(0)
    }
    override suspend fun getLaunchVersion(): Int = 0

    override suspend fun setLaunchVersion(value: Int) { }

    override suspend fun getDatabaseDefaultsVersion(): Int = 0

    override suspend fun setDatabaseDefaultsVersion(value: Int) { }

}