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

package de.xorg.gsapp.ui.tools

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import getPlatformName

val WindowSizeClass.horizontalMargin: Dp
    get() = when(this.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> 24.dp
        else -> 0.dp
    }

fun Modifier.windowSizeMargins(windowSizeClass: WindowSizeClass): Modifier {
    return this.padding(horizontal = windowSizeClass.horizontalMargin)
}

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
fun Modifier.Companion.platformSpecificScrollBehavior(nestedScrollConnection: NestedScrollConnection): Modifier {
    return when(getPlatformName()) {
        "Android" -> this.nestedScroll(nestedScrollConnection)
        else -> this
    }
}