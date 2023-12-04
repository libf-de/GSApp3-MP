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
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalSettingsApi::class)
class MPSettingsRepository : PreferencesRepository, KoinComponent {

    private object PrefKeys {
        const val FilterRole = "role"
        const val FilterValue = "filter"
        const val PushState = "push"
        const val AskNotifyPermission = "askNotifyPermission"
        const val LaunchVersion = "launchVersion"
        const val DatabaseDefaultsVersion = "databaseDefaultsVersion"
    }

    private val appSettings: FlowSettings by inject()

    /**
     * Returns the Filter Role (Student/Teacher/All) from settings
     * @return FilterRole
     */
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    override suspend fun getRole(): Filter.Role {
        return Filter.Role.fromInt(
            appSettings.getInt(PrefKeys.FilterRole, Filter.Role.default.value)
        )
    }

    /**
     * Returns a flow for Filter Role (Student/Teacher/All) from settings
     * @return Flow<FilterRole>
     */
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    override fun getRoleFlow(): Flow<Filter.Role>
            = appSettings.getIntFlow(PrefKeys.FilterRole, Filter.Role.default.value)
        .map { Filter.Role.fromInt(it) }

    /**
     * Stores the Filter Role (Student/Teacher/All) in settings
     * @param value role to store
     */
    @Deprecated("use filter instead", replaceWith = ReplaceWith("setFilter()"))
    override suspend fun setRole(value: Filter.Role) {
        appSettings.putInt(PrefKeys.FilterRole, value.value)
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
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    override suspend fun observeRole(callback: (Filter.Role) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addIntListener(PrefKeys.FilterRole, Filter.Role.default.value) {
            callback(Filter.Role.fromInt(it))
        }
    }

    /**
     * Returns a flow of Filter Value from settings
     * @return flow<String>
     */
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    override fun getFilterValueFlow(): Flow<String>
            = appSettings.getStringFlow(PrefKeys.FilterValue, "")

    /**
     * Returns the Filter Value from settings
     * @return string
     */
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    override suspend fun getFilterValue(): String {
        return appSettings.getString(PrefKeys.FilterValue, "")
    }

    /**
     * Stores the Filter value in settings
     * @param value value to store
     */
    @Deprecated("use filter instead", replaceWith = ReplaceWith("setFilter()"))
    override suspend fun setFilterValue(value: String) {
        appSettings.putString(PrefKeys.FilterValue, value)
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
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    override suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addStringListener(PrefKeys.FilterValue, "") { callback(it) }
    }

    override fun getFilterFlow(): Flow<Filter> = combine(
        appSettings.getIntFlow(PrefKeys.FilterRole, Filter.Role.default.value).map { Filter.Role.fromInt(it) },
        appSettings.getStringFlow(PrefKeys.FilterValue, "")
    ) { role, value ->
        Filter(role ,value)
    }

    override suspend fun getFilter(): Filter {
        return Filter(
            Filter.Role.fromInt(appSettings.getInt(PrefKeys.FilterRole, Filter.Role.default.value)),
            appSettings.getString(PrefKeys.FilterValue, "")
        )
    }

    override suspend fun setFilter(value: Filter) {
        appSettings.putInt(PrefKeys.FilterRole, value.role.value)
        appSettings.putString(PrefKeys.FilterValue, value.value)
    }

    /**
     * Returns a flow for push notification enablement from settings
     * @return Flow<PushState>
     */
    override fun getPushFlow(): Flow<PushState>
            = appSettings.getIntFlow(PrefKeys.PushState, PushState.default.value).map { PushState.fromInt(it) }

    /**
     * Returns the push notification enablement from settings
     * @return PushState
     */
    override suspend fun getPush(): PushState {
        return PushState.fromInt(
            appSettings.getInt(PrefKeys.PushState, PushState.DISABLED.value)
        )
    }

    /**
     * Stores the push notification enablement in settings
     * @param value PushState
     */
    override suspend fun setPush(value: PushState) {
        appSettings.putInt(PrefKeys.PushState, value.value)
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
        return settings.addIntListener(PrefKeys.PushState, PushState.default.value) {
            callback(PushState.fromInt(it))
        }
    }

    override fun getAskUserForNotificationPermissionFlow(): Flow<Boolean>
        = appSettings.getBooleanFlow(PrefKeys.AskNotifyPermission, false)

    override suspend fun getAskUserForNotificationPermission(): Boolean {
        return appSettings.getBoolean(PrefKeys.AskNotifyPermission, false)
    }

    override suspend fun setAskUserForNotificationPermission(value: Boolean) {
        appSettings.putBoolean(PrefKeys.AskNotifyPermission, value)
    }

    override suspend fun observeAskUserForNotificationPermission(callback: (Boolean) -> Unit): SettingsListener? {
        val settings = appSettings as? ObservableSettings ?: return null
        return settings.addBooleanListener(PrefKeys.AskNotifyPermission, false) {
            callback(it)
        }
    }

    /** LaunchVersion - used to determine whether to show onboarding screens and to fill
     * the Database with default values. **/

    /**
     * Returns a flow for launch version from settings.
     * @return Flow<Int>
     */
    override fun getLaunchVersionFlow(): Flow<Int> = appSettings.getIntFlow(PrefKeys.LaunchVersion, 0)

    /**
     * Returns the launch version from settings.
     * @return Int
     */
    override suspend fun getLaunchVersion(): Int {
        return appSettings.getInt(PrefKeys.LaunchVersion, 0)
    }

    /**
     * Stores the launch version in settings.
     * @param value Int
     */
    override suspend fun setLaunchVersion(value: Int) {
        appSettings.putInt(PrefKeys.LaunchVersion, value)
    }

    /**
     * Returns the database defaults version from settings.
     * @return Int
     */
    override suspend fun getDatabaseDefaultsVersion(): Int {
        return appSettings.getInt(PrefKeys.DatabaseDefaultsVersion, 0)
    }

    /**
     * Stores the database defaults version in settings.
     * @param value Int
     */
    override suspend fun setDatabaseDefaultsVersion(value: Int) {
        appSettings.putInt(PrefKeys.DatabaseDefaultsVersion, value)
    }
}