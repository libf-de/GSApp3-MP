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
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.StringResource

/**
 * Stores the selection mode available in the SelectColorDialog
 */
enum class ColorPickerMode(val value: Int): StringResEnum {
    SIMPLE(0) {
        override val labelResource: StringResource = MR.strings.dialog_color_simple
        override val descriptiveResource: StringResource = MR.strings.dialog_color_simple_desc
    },
    ADVANCED(1) {
        override val labelResource: StringResource = MR.strings.dialog_color_advanced
        override val descriptiveResource: StringResource = MR.strings.dialog_color_advanced_desc
    };

    companion object {
        val default = SIMPLE

        fun fromInt(value: Int): ColorPickerMode
                = ColorPickerMode.values().firstOrNull { it.value == value } ?: default
    }
}