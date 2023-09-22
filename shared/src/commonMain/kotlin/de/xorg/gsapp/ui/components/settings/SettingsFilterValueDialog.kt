/*
 * This file was cherry-picked from MensaApp (https://github.com/mensa-app-wuerzburg/Android)
 * Used with permission.
 * Copyright (C) 2023 Erik Spall
 *               2023 Fabian Schillig
 */

package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.enums.StringResEnum
import de.xorg.gsapp.data.model.Teacher
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SettingsFilterTeacherDialog(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    items: List<Teacher>,
    selectedValue: String,
    dismissText: String,
    confirmText: String,
    onConfirm: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    //val openDialog = remember { mutableStateOf(true) }
    val selected = remember { mutableStateOf(selectedValue) }

    val dropdownOpen = remember { mutableStateOf(false) }
    val dropdownValue = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismiss()
        },
        icon = { icon() },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                Text(text = message)
                Divider(
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
                Column {
                    OutlinedButton(onClick = { dropdownOpen.value = !dropdownOpen.value }) {
                        Text(dropdownValue.value)
                    }

                    DropdownMenu(
                        expanded = dropdownOpen.value,
                        onDismissRequest = { dropdownOpen.value = false }
                    ) {
                        DropdownMenuItem(text = { Text("Foo") }, onClick = {
                            dropdownValue.value = "Foo"
                            dropdownOpen.value = false
                        })
                    }
                }
                Divider(
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selected.value)
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(dismissText)
            }
        }
    )
}

/*@Preview
@Composable
fun PreviewSettingsRadioDialog() {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        SettingsRadioDialog(
            showDialog = showDialog.value,
            iconVector = Icons.Rounded.Place,
            title = "Standort wählen",
            confirmText = "Speichern",
            dismissText = "Zurück",
            message = "Wähle hier welchen Standort du sehen möchtest",
            onConfirm = {
                showDialog.value = false
            },
            onDismiss = {
                showDialog.value = false
            },
            items = Location.values().toList(),
            selectedValue = Location.WUERZBURG
        )
    }

}*/