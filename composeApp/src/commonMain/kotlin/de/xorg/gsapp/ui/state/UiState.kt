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

import de.xorg.gsapp.data.model.ComponentData

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

fun UiState.isNormal(): Boolean {
    return this == UiState.NORMAL || this == UiState.NORMAL_LOADING || this == UiState.NORMAL_FAILED
}

sealed class ComponentState<out T : ComponentData, out E : Throwable> {
    data object Loading : ComponentState<Nothing, Nothing>()
    data class Refreshing<out T : ComponentData>(val data: T) : ComponentState<T, Nothing>()
    data class RefreshingFailed<out T : ComponentData, out E : Throwable>(val data: T, val error: E) : ComponentState<T, E>()
    data class Failed<out E : Throwable>(val error: E) : ComponentState<Nothing, E>()
    data object EmptyLocal : ComponentState<Nothing, Nothing>()
    data object Empty : ComponentState<Nothing, Nothing>()
    data class Normal<out T : ComponentData>(val data: T) : ComponentState<T, Nothing>()

    fun hasData(): Boolean {
        return this is Refreshing || this is RefreshingFailed || this is Normal
    }

    fun isLoading(): Boolean {
        return this is Loading || this is Refreshing
    }

    fun isNormal(): Boolean {
        return this is Normal || this is Refreshing || this is RefreshingFailed
    }
}