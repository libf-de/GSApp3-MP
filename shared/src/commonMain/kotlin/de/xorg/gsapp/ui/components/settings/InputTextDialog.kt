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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.ColorPickerMode
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun InputTextDialog(
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit,
    title: String? = null,
    message: String? = null,
    value: String? = null,
    placeholder: String? = null
) {
    var inputValue by remember { mutableStateOf(value ?: "") }

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

                TextField(
                    value = inputValue,
                    onValueChange = { text -> inputValue = text},
                    placeholder = { if(placeholder != null) Text(placeholder) },
                    isError = inputValue.isBlank()
                )
            }
        },
        confirmButton = {
            if(inputValue.isNotBlank())
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
        }
    )
}