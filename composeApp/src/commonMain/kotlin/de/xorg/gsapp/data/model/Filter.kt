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

package de.xorg.gsapp.data.model

import de.xorg.gsapp.data.enums.StringResEnum
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.filter_all
import gsapp.composeapp.generated.resources.filter_student
import gsapp.composeapp.generated.resources.filter_teacher
import gsapp.composeapp.generated.resources.pref_filter_all
import gsapp.composeapp.generated.resources.pref_filter_student
import gsapp.composeapp.generated.resources.pref_filter_teacher
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

data class Filter(
    val role: Role,
    val value: String
) {
    companion object {
        val NONE = Filter(Role.ALL, "")
    }

    fun substitutionMatches(substitution: Substitution): Boolean {
        return when(role) {
            Role.ALL -> true
            Role.TEACHER -> substitution.substTeacher.shortName.lowercase() == value.lowercase()
            Role.STUDENT -> substitution.klassFilter.lowercase().contains(value.lowercase())
        }
    }


    /**
     * The filter roles for filtering the substitution plan, either by not filtering (ALL),
     * or only showing the specified Teacher (TEACHER) / Class (STUDENT).
     * Also contains human-readable labels and descriptions for the associated settings dialog.
     */
    @OptIn(ExperimentalResourceApi::class)
    enum class Role(val value: Int): StringResEnum {
        ALL(0) {
            override val labelResource: StringResource = Res.string.filter_all
            override val descriptiveResource: StringResource = Res.string.pref_filter_all
        },
        TEACHER(1) {
            override val labelResource: StringResource = Res.string.filter_teacher
            override val descriptiveResource: StringResource = Res.string.pref_filter_teacher
        },
        STUDENT(2) {
            override val labelResource: StringResource = Res.string.filter_student
            override val descriptiveResource: StringResource = Res.string.pref_filter_student
        };

        companion object {
            val default = ALL

            fun shouldStore(role: Role): Boolean = when(role) {
                ALL -> true
                TEACHER -> false
                STUDENT -> false
            }

            fun fromInt(value: Int): Role
                    = entries.firstOrNull { it.value == value } ?: default
        }
    }
}
