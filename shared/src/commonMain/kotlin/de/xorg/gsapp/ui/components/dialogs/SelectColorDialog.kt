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

package de.xorg.gsapp.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.ColorListPicker
import de.xorg.gsapp.ui.components.settings.ColorPicker
import de.xorg.gsapp.ui.materialtools.MaterialColors
import de.xorg.gsapp.ui.state.ColorPickerMode
import de.xorg.gsapp.ui.tools.ScreenOrientation
import de.xorg.gsapp.ui.tools.foregroundColorForBackground
import de.xorg.gsapp.ui.tools.getOrientation
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SelectColorDialog(
    onConfirm: (Color) -> Unit,
    onCancel: () -> Unit,
    showButtons: Boolean = true,
    preselectedColor: Color? = null,
    title: String? = null,
    message: String? = null,
    pickMode: ColorPickerMode = ColorPickerMode.default,
    onPickModeChanged: (ColorPickerMode) -> Unit = { _ -> }
) {
    val selectedColor = remember { mutableStateOf(preselectedColor) }
    val selectedHarmonizedColor = remember { mutableStateOf(preselectedColor) }
    val selectedPickMode = remember { mutableStateOf(pickMode) }

    val orientation = remember { mutableStateOf(ScreenOrientation.SQUARE) }

    LaunchedEffect(selectedPickMode.value) {
        onPickModeChanged(selectedPickMode.value)
    }

    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    LaunchedEffect(selectedColor.value, MaterialTheme.colorScheme.primary) {
        selectedHarmonizedColor.value = selectedColor
            .value
            ?.toArgb()
            ?.let {
                MaterialColors.harmonize(
                    colorToHarmonize = it,
                    colorToHarmonizeWith = primaryColor
                )
            }
            ?.let { Color(it) }
    }

    /**
     * Bodge-fix to get window/screen orientation
     * TODO: Use proper way if there is one.
     */
    Box(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            orientation.value = it.getOrientation()
        }
    )

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            if(title != null)
                Text(text = title)
        },
        text = {
            Column {
                if(message != null)
                    Text(text = message)

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly) {

                    ColorPickerMode.values().forEach {
                        Row(modifier = Modifier.selectable(
                            selected = selectedPickMode.value == it,
                            onClick = {
                                selectedPickMode.value = it
                            }
                        )) {
                            RadioButton(selected = (selectedPickMode.value == it),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(modifier = Modifier.padding(start = 4.dp).height(
                                IntrinsicSize.Max),
                                text = stringResource(it.labelResource) )
                        }
                    }
                }

                when(selectedPickMode.value) {
                    ColorPickerMode.SIMPLE -> {
                        ColorListPicker(colorState = selectedColor)
                    }

                    ColorPickerMode.ADVANCED -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ColorPicker(
                                colorState = selectedColor,
                                orientation = orientation.value,
                                maxDim = 200,
                                modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        },
        confirmButton = {
            if(showButtons) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedHarmonizedColor.value ?: Color.Transparent,
                        contentColor = foregroundColorForBackground(selectedHarmonizedColor.value)
                    ),
                    onClick = {
                        onConfirm(selectedHarmonizedColor.value ?: Color.Unspecified)
                    }
                ) {
                    Text(
                        text = stringResource(MR.strings.dialog_save)
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(
                    text = stringResource(MR.strings.dialog_cancel)
                )
            }
        }
    )
}