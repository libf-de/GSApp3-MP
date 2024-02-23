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

package de.xorg.gsapp.ui.state

import de.xorg.gsapp.data.enums.StringResEnum
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.push_disabled
import gsapp.composeapp.generated.resources.push_enabled
import gsapp.composeapp.generated.resources.push_filter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

/**
 * The possible Push Notification settings. Either no push notifications (DISABLED), or only
 * notifying when there are substitutions matching the configured filter (LIKE_FILTER) or always
 * notifying (ENABLED).
 * Also contains human-readable labels and descriptions for the associated settings dialog.
 */
@OptIn(ExperimentalResourceApi::class)
enum class PushState(val value: Int) : StringResEnum {
    DISABLED(0) {
        override val labelResource: StringResource = Res.string.push_disabled
        override val descriptiveResource: StringResource = labelResource
    },
    LIKE_FILTER(1) {
        override val labelResource: StringResource = Res.string.push_filter
        override val descriptiveResource: StringResource = labelResource
    },
    ENABLED(2) {
        override val labelResource: StringResource = Res.string.push_enabled
        override val descriptiveResource: StringResource = labelResource
    };

    companion object {
        val default = DISABLED

        fun fromInt(value: Int): PushState
                = entries.firstOrNull { it.value == value } ?: default
    }
}