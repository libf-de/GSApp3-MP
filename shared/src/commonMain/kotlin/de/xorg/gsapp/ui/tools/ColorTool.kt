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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun foregroundColorForBackground(backgroundColor: Color?): Color {
    if(backgroundColor == null) return MaterialTheme.colorScheme.primary

    val luminance = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue)
    return if (luminance > 0.5) Color.Black else Color.White
}

@OptIn(ExperimentalStdlibApi::class)
fun Color?.toHexString(): String {
    if(this == null) return ""

    val red = (this.red * 255).toInt().toHexString(HexFormat.UpperCase).takeLast(2)
    val green = (this.green * 255).toInt().toHexString(HexFormat.UpperCase).takeLast(2)
    val blue = (this.blue * 255).toInt().toHexString(HexFormat.UpperCase).takeLast(2)
    return "#${red}${green}${blue}"
}

