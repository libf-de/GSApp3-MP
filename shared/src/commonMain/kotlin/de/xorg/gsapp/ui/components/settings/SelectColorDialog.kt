package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.ColorPickerMode
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
    val selectedPickMode = remember { mutableStateOf(pickMode) }

    LaunchedEffect(selectedPickMode.value) {
        onPickModeChanged(selectedPickMode.value)
    }

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
                            ColorPicker(selectedColor, modifier = Modifier.width(300.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            if(showButtons)
                TextButton(
                    enabled = selectedColor.value != null,
                    onClick = {
                        onConfirm(selectedColor.value ?: Color.Unspecified)
                    }
                ) {
                    Text(
                        text = stringResource(MR.strings.dialog_save)
                    )
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