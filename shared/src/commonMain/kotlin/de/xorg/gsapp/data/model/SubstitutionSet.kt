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

import kotlinx.datetime.LocalDate

/**
 * A complete representation of a substitution plan, consisting of "global" date and notes, and
 * a mapping of Class -> List<Substitution>
 *
 * This uses a map for the substitutions, this makes it easier to generate the composables.
 *
 * @param date string representation of the date, as displayed on the website
 * @param notes as displayed on website
 * @param substitutions map of classes to List<Substitution>
 */
data class SubstitutionSet(
    val dateStr: String,
    val date: LocalDate,
    val notes: String,
    val substitutions: Map<String, List<Substitution>>,
    val haveUnknownSubs: Boolean = false,
    val haveUnknownTeachers: Boolean = false
) {

    /**
     * Constructor for an empty SubstitutionSet, used to initialize the app state.
     */
    constructor() : this(
        dateStr = "",
        date = LocalDate.fromEpochDays(0),
        notes = "",
        substitutions = emptyMap())
}