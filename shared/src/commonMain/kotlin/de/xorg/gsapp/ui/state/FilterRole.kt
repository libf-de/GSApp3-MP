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
 * The filter roles for filtering the substitution plan, either by not filtering (ALL),
 * or only showing the specified Teacher (TEACHER) / Class (STUDENT).
 * Also contains human-readable labels and descriptions for the associated settings dialog.
 */
enum class FilterRole(val value: Int): StringResEnum {
    ALL(0) {
        override val labelResource: StringResource = MR.strings.filter_all
        override val descriptiveResource: StringResource = MR.strings.pref_filter_all
    },
    TEACHER(1) {
        override val labelResource: StringResource = MR.strings.filter_teacher
        override val descriptiveResource: StringResource = MR.strings.pref_filter_teacher
    },
    STUDENT(2) {
        override val labelResource: StringResource = MR.strings.filter_student
        override val descriptiveResource: StringResource = MR.strings.pref_filter_student
    };

    companion object {
        val default = ALL

        fun shouldStore(role: FilterRole): Boolean = when(role) {
            ALL -> true
            TEACHER -> false
            STUDENT -> false
        }

        fun fromInt(value: Int): FilterRole
            = FilterRole.values().firstOrNull { it.value == value } ?: default
    }
}