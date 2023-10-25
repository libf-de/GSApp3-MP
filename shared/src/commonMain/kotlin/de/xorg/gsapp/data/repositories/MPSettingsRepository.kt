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

package de.xorg.gsapp.data.repositories

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.coroutines.FlowSettings
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance

@OptIn(ExperimentalSettingsApi::class)
class MPSettingsRepository(di: DI) : PreferencesRepository {

    //private val appSettings: Settings by di.instance()
    private val appSettings: FlowSettings by di.instance()

    /**
     * Returns the Filter Role (Student/Teacher/All) from settings
     * @return FilterRole
     */
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getRoleFlow()"))
    override suspend fun getRole(): FilterRole {
        return FilterRole.fromInt(
            appSettings.getInt("role", FilterRole.default.value)
        )
    }

    /**
     * Returns a flow for Filter Role (Student/Teacher/All) from settings
     * @return Flow<FilterRole>
     */
    override fun getRoleFlow(): Flow<FilterRole>
            = appSettings.getIntFlow("role", FilterRole.default.value)
        .map { FilterRole.fromInt(it) }

    /**
     * Stores the Filter Role (Student/Teacher/All) in settings
     * @param value role to store
     */
    override suspend fun setRole(value: FilterRole) {
        appSettings.putInt("role", value.value)
    }

    /**
     * Observes the filter role setting for changes, and runs the given callback
     * after the setting was changed.
     *
     * IMPORTANT: A strong reference to the returned SettingsListener must be held, otherwise
     * updates might not be sent! (https://github.com/russhwolf/multiplatform-settings#listeners)
     *
     * @param callback function to run when role was changed.
     * @return reference to the SettingsListener
     */
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getRoleFlow()"))
    override suspend fun observeRole(callback: (FilterRole) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener("role", FilterRole.default.value) {
            callback(FilterRole.fromInt(it))
        }
    }

    /**
     * Returns a flow of Filter Value from settings
     * @return flow<String>
     */
    override fun getFilterValueFlow(): Flow<String>
            = appSettings.getStringFlow("filter", "")

    /**
     * Returns the Filter Value from settings
     * @return string
     */
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getFilterValueFlow()"))
    override suspend fun getFilterValue(): String {
        return appSettings.getString("filter", "")
    }

    /**
     * Stores the Filter value in settings
     * @param value value to store
     */
    override suspend fun setFilterValue(value: String) {
        appSettings.putString("filter", value)
    }

    /**
     * Observes the filter value setting for changes, and runs the given callback
     * after the setting was changed.
     *
     * IMPORTANT: A strong reference to the returned SettingsListener must be held, otherwise
     * updates might not be sent! (https://github.com/russhwolf/multiplatform-settings#listeners)
     *
     * @param callback function to run when value was changed.
     * @return reference to the SettingsListener
     */
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getFilterValueFlow()"))
    override suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addStringListener("filter", "") { callback(it) }
    }

    /**
     * Returns a flow for push notification enablement from settings
     * @return Flow<PushState>
     */
    override fun getPushFlow(): Flow<PushState>
            = appSettings.getIntFlow("push", PushState.default.value).map { PushState.fromInt(it) }

    /**
     * Returns the push notification enablement from settings
     * @return PushState
     */
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getPushFlow()"))
    override suspend fun getPush(): PushState {
        return PushState.fromInt(
            appSettings.getInt("push", PushState.DISABLED.value)
        )
    }

    /**
     * Stores the push notification enablement in settings
     * @param value PushState
     */
    override suspend fun setPush(value: PushState) {
        appSettings.putInt("push", value.value)
    }

    /**
     * Observes the push notification enablement setting for changes, and runs the given callback
     * after the setting was changed.
     *
     * IMPORTANT: A strong reference to the returned SettingsListener must be held, otherwise
     * updates might not be sent! (https://github.com/russhwolf/multiplatform-settings#listeners)
     *
     * @param callback function to run when PushState was changed.
     * @return reference to the SettingsListener
     */
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getPushFlow()"))
    override suspend fun observePush(callback: (PushState) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener("push", PushState.default.value) {
            callback(PushState.fromInt(it))
        }
    }
}