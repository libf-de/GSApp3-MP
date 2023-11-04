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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun InputTextDialog(
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
    title: String? = null,
    message: String? = null,
    value: String? = null,
    placeholder: String? = null,
    label: String? = null,
    modifier: Modifier = Modifier
) {
    var inputValue by remember { mutableStateOf(value ?: "") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            if(title != null)
                Text(text = title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if(message != null)
                    Text(text = message)

                OutlinedTextField(
                    value = inputValue,
                    label = { if(label != null) Text(label) },
                    onValueChange = { text -> inputValue = text.capitalize(Locale.current) },
                    placeholder = { if(placeholder != null) Text(placeholder) },
                    isError = inputValue.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = inputValue.isNotBlank(),
                onClick = {
                    onConfirm(inputValue)
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
        },
        modifier = modifier
    )
}