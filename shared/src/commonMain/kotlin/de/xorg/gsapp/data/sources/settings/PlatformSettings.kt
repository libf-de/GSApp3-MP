package de.xorg.gsapp.data.sources.settings

import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState

expect class PlatformSettings

expect suspend fun PlatformSettings.getInt(key: String, default: Int = 0): Int
expect suspend fun PlatformSettings.setInt(key: String, value: Int)

expect suspend fun PlatformSettings.getString(key: String, default: String = ""): String
expect suspend fun PlatformSettings.setString(key: String, value: String)

class PreferenceKeys {
    companion object {
        const val ROLE = "filterRole"
        const val FILTERVALUE = "filterValue"
        const val PUSH = "pushState"
    }
}

class SettingsSource(private val platformSettings: PlatformSettings) {
    suspend fun getRole(): FilterRole {
        return FilterRole.fromInt(platformSettings.getInt(PreferenceKeys.ROLE))
    }

    suspend fun setRole(value: FilterRole) {
        platformSettings.setInt(PreferenceKeys.ROLE, value.value)
    }

    suspend fun getFilterValue(): String {
        return platformSettings.getString(PreferenceKeys.FILTERVALUE)
    }

    suspend fun setFilterValue(value: String) {
        platformSettings.setString(PreferenceKeys.FILTERVALUE, value)
    }

    suspend fun getPush(): PushState {
        return PushState.fromInt(platformSettings.getInt(PreferenceKeys.PUSH))
    }

    suspend fun setPush(value: PushState) {
        platformSettings.setInt(PreferenceKeys.PUSH, value.value)
    }
}