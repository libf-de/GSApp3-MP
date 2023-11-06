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

/**
 * Loading states for the main app tabs.
 */
enum class UiState {
    LOADING, // Show loading component
    NORMAL_LOADING, // Data loaded, but also still refreshing
    NORMAL_FAILED, // Data loaded, but refreshing failed
    FAILED, // An error occurred, and no data is available. If there is data available, the app currently fails silently. TODO: Don't fail silently!!!
    EMPTY_LOCAL, // There is no local data available.
    EMPTY, // There are no entries in the loaded plan.
    NORMAL // Data was loaded successfully, display the data!
}

fun UiState.isLoading(): Boolean {
    return this == UiState.LOADING || this == UiState.NORMAL_LOADING
}