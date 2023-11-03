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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.enums.StringResEnum
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

/**
 * This is an AlertDialog with a title, message and some radio buttons.
 * Currently used for push settings.
 */

@Composable
fun SettingsRadioDialog(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    items: ImmutableList<StringResEnum>,
    selectedValue: StringResEnum,
    dismissText: String,
    confirmText: String,
    onConfirm: (StringResEnum) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val selected = remember { mutableStateOf(selectedValue) }

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
                    for (item in items) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selected.value == item),
                                    onClick = { selected.value = item },
                                    role = Role.RadioButton
                                )
                                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selected.value == item),
                                onClick = null // null recommended for accessibility with screenreaders
                            )
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = stringResource(item.labelResource),
                            )
                        }
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