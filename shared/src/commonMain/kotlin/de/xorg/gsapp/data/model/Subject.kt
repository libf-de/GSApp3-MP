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

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.data.DbSubject

/**
 * Data class to hold a single subject
 * @property shortName short name of the subject, as used in substitution plan
 * @property longName long name, as it will be displayed to the user
 * @property color to color the cards of this subject with
 */
data class Subject(
    val shortName: String,
    val longName: String,
    val color: Color
) {
    companion object {
        val emptySubject = Subject("")
    }

    /**
     * Constructor for "unknown" placeholder subject, will be marked in magenta.
     * Also has no long name, so the shortName will be displayed
     */
    constructor(shortName: String) : this(shortName, shortName, Color.Magenta)

    constructor(dbSubject: DbSubject) : this(shortName = dbSubject.shortName,
                                             longName = dbSubject.longName,
                                             color = dbSubject.color ?: Color.Unspecified)

    /**
     * By default, two Subjects are the same if they have the same shortName.
     * This is to easily merge app defaults and user preferences.
     */
    /*override fun equals(other: Any?): Boolean {
        if(other !is Subject) return false
        return other.shortName == this.shortName
    }*/

    /**
     * Checks if a given Subject is not the empty Subject (=blank shortName)
     */
    fun isBlank(): Boolean = this.shortName.isBlank()
    fun isNotBlank(): Boolean = this.shortName.isNotBlank()
}
