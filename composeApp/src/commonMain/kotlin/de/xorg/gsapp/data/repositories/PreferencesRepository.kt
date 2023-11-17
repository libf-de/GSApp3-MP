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

import com.russhwolf.settings.SettingsListener
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    /**
     * These shall return the value stored in settings, store the given value in settings or
     * setup an observer that triggers when the setting was changed. See implementation
     * for further documentation
     */

    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    fun getRoleFlow(): Flow<Filter.Role>

    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    suspend fun getRole(): Filter.Role
    @Deprecated("use filter instead", replaceWith = ReplaceWith("setFilter()"))
    suspend fun setRole(value: Filter.Role)
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    suspend fun observeRole(callback: (Filter.Role) -> Unit): SettingsListener?

    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    fun getFilterValueFlow(): Flow<String>
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    suspend fun getFilterValue(): String
    @Deprecated("use filter instead", replaceWith = ReplaceWith("setFilter()"))
    suspend fun setFilterValue(value: String)
    @Deprecated("use filter instead", replaceWith = ReplaceWith("getFilterFlow()"))
    suspend fun observeFilterValue(callback: (String) -> Unit): SettingsListener?


    fun getFilterFlow(): Flow<Filter>
    suspend fun getFilter(): Filter
    suspend fun setFilter(value: Filter)


    fun getPushFlow(): Flow<PushState>
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getPushFlow()"))
    suspend fun getPush(): PushState
    suspend fun setPush(value: PushState)
    @Deprecated("use flow instead", replaceWith = ReplaceWith("getPushFlow()"))
    suspend fun observePush(callback: (PushState) -> Unit): SettingsListener?

    fun getAskUserForNotificationPermissionFlow(): Flow<Boolean>
    suspend fun getAskUserForNotificationPermission(): Boolean
    suspend fun setAskUserForNotificationPermission(value: Boolean)
    suspend fun observeAskUserForNotificationPermission(callback: (Boolean) -> Unit): SettingsListener?

    fun getLaunchVersionFlow(): Flow<Int>
    suspend fun getLaunchVersion(): Int
    suspend fun setLaunchVersion(value: Int)

    suspend fun getDatabaseDefaultsVersion(): Int
    suspend fun setDatabaseDefaultsVersion(value: Int)
}