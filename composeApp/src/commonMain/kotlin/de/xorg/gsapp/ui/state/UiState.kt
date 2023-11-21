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

import androidx.compose.runtime.Composable
import de.xorg.gsapp.data.model.SubstitutionSet

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

// <out T>, because we want to be able to return a ComponentState<Nothing> as well.
sealed class ComponentState<out T> {
    data object Loading : ComponentState<Nothing>()
    data object EmptyLocal : ComponentState<Nothing>()
    data object Empty : ComponentState<Nothing>()

    abstract class StateWithData<T> : ComponentState<T>() {
        abstract val data: T
    }
    data class Normal<T>(override val data: T) : StateWithData<T>()
    data class Refreshing<T>(override val data: T) : StateWithData<T>()
    data class RefreshingFailed<T>(override val data: T, val error: Throwable) : StateWithData<T>()

    data class Failed(val error: Throwable) : ComponentState<Nothing>()



    @Composable
    fun whenDataAvailable(composable: @Composable (data: T) -> Unit): ComponentState<T> {
        when(this) {
            is Refreshing -> composable(data)
            is RefreshingFailed<T> -> composable(data)
            is Normal -> composable(data)
            else -> {}
        }
        return this
    }

    @Composable
    fun whenErrorAvailable(composable: @Composable (error: Throwable) -> Unit): ComponentState<T>  {
        when(this) {
            is RefreshingFailed<T> -> composable(error)
            is Failed -> composable(error)
            else -> {}
        }
        return this
    }

    fun hasData(): Boolean {
        return this is Refreshing || this is RefreshingFailed<*> || this is Normal
    }

    fun isLoading(): Boolean {
        return this is Loading || this is Refreshing
    }

    fun isNormal(): Boolean {
        return this is Normal || this is Refreshing || this is RefreshingFailed<*>
    }

    fun ensureNotStuck(): ComponentState<T> {
        return when(this) {
            is Loading -> EmptyLocal /* Should not happen */
            is Refreshing -> Normal(this.data)
            is RefreshingFailed<T> -> Normal(this.data)
            else -> this
        }
    }

    fun toFailureState(it: Throwable): ComponentState<T> {
        return if(this is Refreshing) {
            RefreshingFailed(this.data, it)
        } else {
            Failed(it)
        }
    }

    companion object {
        /**
         * Create a ComponentState from a collection (List, Set, Map, ...) that can be empty.
         * If the collection is empty, return [Empty], otherwise return [Normal].
         * @param data The collection to check.
         * @return [Empty] if the collection is empty, [Normal] otherwise.
         */
        fun <T> fromEmptyable(data: T): ComponentState<T> {
            return if (data is Collection<*> && data.isEmpty()) Empty
            else if (data is Map<*,*> && data.isEmpty()) Empty
            else Normal(data)
        }

    }
}

fun <T> ComponentState<T>.dataOrDefault(default: T): T {
    return when(this) {
        is ComponentState.Normal -> data
        else -> default
    }
}