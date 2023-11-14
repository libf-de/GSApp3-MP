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

package de.xorg.gsapp.ui.components.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.tools.windowSizeMargins
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun EmptyLocalComponent(
    where: StringResource,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier.fillMaxSize().windowSizeMargins(windowSizeClass)
) {
    EmptyLocalComponent(stringResource(where), modifier)
}


@Composable
fun EmptyLocalComponent(
    where: StringResource,
    modifier: Modifier = Modifier
) {
    EmptyLocalComponent(stringResource(where), modifier)
}

@Composable
fun EmptyLocalComponent(
    where: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(
                MR.strings.empty_local,
                where
            )
        )
    }
}