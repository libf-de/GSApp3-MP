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

package de.xorg.gsapp.data.push

import org.koin.core.component.KoinComponent

/**
 * Used to interact with the platform-specific push notification services.
 * Also denotes whether a platform even supports them, to disable the setting if unsupported.
 */

interface PushNotificationUtil : KoinComponent {

    val isSupported: Boolean

    fun enablePushService(callback: (success: Boolean) -> Unit)

    fun disablePushService(callback: (success: Boolean) -> Unit)

    fun ensurePushPermissions(callback: (success: Boolean) -> Unit)
}