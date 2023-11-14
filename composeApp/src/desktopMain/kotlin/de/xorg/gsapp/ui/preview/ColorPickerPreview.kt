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

package de.xorg.gsapp.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.ui.components.settings.ColorPicker
import de.xorg.gsapp.ui.tools.ScreenOrientation

@Composable
@Preview
fun ColorPickerPreview() {
    var colorState = remember { mutableStateOf<Color?>(Color.Green)}

    ColorPicker(
        colorState = colorState,
        orientation = ScreenOrientation.LANDSCAPE,
        maxDim = 300,
        modifier = Modifier.width(300.dp))
}