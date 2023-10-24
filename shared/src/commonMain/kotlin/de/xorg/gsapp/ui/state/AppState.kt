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

import de.xorg.gsapp.data.exceptions.NoException

/**
 * This data class represents the main-app ui-states for all tabs.
 * It holds loading states, whether a tab is currently reloading and the error that might have
 * occurred while loading, as well as the selected filter for substitution plan.
 */
data class AppState(
    val substitutionState: UiState = UiState.LOADING,
    val substitutionReloading: Boolean = false,
    val substitutionError: Throwable = NoException(),
    val filterRole: FilterRole = FilterRole.ALL,
    val filter: String = "",

    val foodplanState: UiState = UiState.LOADING,
    val foodplanReloading: Boolean = false,
    val foodplanError: Throwable = NoException(),

    val examState: UiState = UiState.LOADING,
    val examReloading: Boolean = false,
    val examError: Throwable = NoException(),
)
