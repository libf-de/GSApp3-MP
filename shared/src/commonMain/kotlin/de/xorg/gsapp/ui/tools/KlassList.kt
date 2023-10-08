/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
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

package de.xorg.gsapp.ui.tools

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Contains the list of possible classes.
 * Automatically appends the abitur classes for current, next and the year after next (A23…)
 */

private val curYear: Int = Clock.System.todayIn(TimeZone.currentSystemDefault()).year % 100
val classList: ImmutableList<String> = persistentListOf(
        "5.1", "5.2", "5.3", "5.4", "5.5",
        "6.1", "6.2", "6.3", "6.4", "6.5",
        "7.1", "7.2", "7.3", "7.4", "7.5",
        "8.1", "8.2", "8.3", "8.4", "8.5",
        "9.1", "9.2", "9.3", "9.4", "9.5",
        "10.1", "10.2", "10.3", "10.4", "10.5",
        "A${curYear}", "A${curYear+1}", "A${curYear+2}")

