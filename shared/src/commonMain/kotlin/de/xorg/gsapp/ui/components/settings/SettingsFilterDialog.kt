/*
 * This file was cherry-picked from MensaApp (https://github.com/mensa-app-wuerzburg/Android)
 * Used with permission.
 * Copyright (C) 2023 Erik Spall
 *               2023 Fabian Schillig
 */

package de.xorg.gsapp.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.tools.LETTERS
import de.xorg.gsapp.ui.tools.classList
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SettingsFilterDialog(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    teacherState: UiState,
    teacherList: List<Teacher>,
    selectedValue: String,
    dismissText: String,
    confirmText: String,
    onConfirm: (FilterRole, String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {}
) {
    // FilterValue to store in settings
    // (Teacher shortName / Student class)
    var filterVal by remember { mutableStateOf(selectedValue) }

    // Selected user role (Any/Student/Teacher)
    var roleVal by remember { mutableStateOf(FilterRole.ALL) }

    /** Specific for Teacher **/
    //To focus both TextFields (long/short) at the same time
    val inputInteractionSource = remember { MutableInteractionSource() }
    //Long teacher name display value (right)
    var teacherLong by remember { mutableStateOf("") }
    var teacherCandidate: Teacher? by remember { mutableStateOf(null) }

    val confirmFocusReq = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        icon = { icon() },
        title = { Text(text = title) },
        text = {
            Column {
                Text(text = message, modifier = Modifier.padding(bottom = 8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterRole.entries.forEach {
                        Row(modifier = Modifier.selectable(
                            selected = roleVal == it,
                            onClick = { roleVal = it }
                        )) {
                            RadioButton(selected = (roleVal == it),
                                        onClick = null // null recommended for accessibility with screenreaders
                            )
                            Text(modifier = Modifier.padding(start = 4.dp).height(IntrinsicSize.Max),
                                 text = stringResource(it.labelResource) )
                        }
                    }
                }

                /**** Begin TeacherSection **/
                AnimatedVisibility(visible = roleVal == FilterRole.TEACHER) {
                    Column {
                        Box(modifier = Modifier.padding(bottom = 8.dp)) {
                            OutlinedTextField(
                                value = teacherLong,
                                onValueChange = { },
                                readOnly = false,
                                label = { },
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                                interactionSource = inputInteractionSource,
                                trailingIcon = {
                                    IconButton(onClick = {
                                        teacherLong = ""
                                        filterVal = ""
                                    }) {
                                        Icon(Icons.Default.Clear, "")
                                    }
                                },
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth().drawWithContent {
                                    if (layoutDirection == LayoutDirection.Rtl) {
                                        clipRect(right = size.width / 3f) {
                                            this@drawWithContent.drawContent()
                                        }
                                    } else {
                                        clipRect(left = size.width / 3f) {
                                            this@drawWithContent.drawContent()
                                        }
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = filterVal,
                                onValueChange = {
                                    filterVal = it.uppercase()
                                    teacherCandidate = teacherList.firstOrNull { teacher ->
                                        return@firstOrNull teacher.shortName == filterVal
                                    }
                                    teacherLong = teacherCandidate?.longName ?: ""
                                },
                                label = { Text(text = "Lehrerkürzel") },
                                interactionSource = inputInteractionSource,
                                maxLines = 1,
                                trailingIcon = {
                                    IconButton(onClick = { teacherLong = ""; filterVal = "" }) {
                                        Icon(Icons.Default.Clear, "")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .onKeyEvent {
                                        println(it.key)
                                        if (it.key == Key.Enter) {
                                            if (teacherCandidate != null)
                                                confirmFocusReq.requestFocus()
                                            return@onKeyEvent true
                                        }
                                        if (!LETTERS.contains(it.key)) return@onKeyEvent true
                                        return@onKeyEvent false
                                    }
                                    .drawWithContent {
                                        if (layoutDirection == LayoutDirection.Rtl) {
                                            clipRect(left = size.width * 2 / 3f) {
                                                this@drawWithContent.drawContent()
                                            }
                                        } else {
                                            clipRect(right = size.width * 2 / 3f) {
                                                this@drawWithContent.drawContent()
                                            }
                                        }
                                    }
                            )

                        }
                        when (teacherState) {
                            UiState.LOADING -> LinearProgressIndicator()
                            UiState.EMPTY -> Text("Es wurden keine Lehrer gefunden, bitte geben Sie ihr Kürzel von Hand ein.")
                            UiState.FAILED -> Text("Lehrer konnten nicht geladen werden, bitte geben Sie ihr Kürzel von Hand ein.")
                            else -> {
                                if (teacherCandidate == null)
                                    LazyColumn(modifier = Modifier) {
                                        item {
                                            Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
                                        }
                                        teacherList.filter {
                                            return@filter it.shortName
                                                .lowercase()
                                                .contains(filterVal.lowercase())
                                                    || it.longName.contains(filterVal.lowercase())
                                        }.forEach {
                                            item {
                                                Column(
                                                    Modifier.clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        onClick = {
                                                            filterVal = it.shortName.uppercase()
                                                            teacherLong = it.longName

                                                        },
                                                        indication = rememberRipple(bounded = true)
                                                    )
                                                ) {
                                                    Text(
                                                        text = "${it.shortName} ⸺ ${it.longName}",
                                                        modifier = Modifier.padding(12.dp),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = if (it.longName == teacherLong)
                                                            MaterialTheme.colorScheme.primary
                                                        else Color.Unspecified
                                                    )
                                                    Divider(
                                                        modifier = Modifier.fillMaxWidth()
                                                            .height(1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }
                } /** End TeacherSection ****/

                /**** Begin StudentSection **/
                AnimatedVisibility(visible = roleVal == FilterRole.STUDENT) {
                    LazyColumn(modifier = Modifier) {
                        item {
                            Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
                        }
                        classList.forEach {
                            item {
                                Column(
                                    Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            filterVal = it
                                        },
                                        indication = rememberRipple(bounded = true)
                                    )
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(12.dp),
                                        fontWeight = if (it == filterVal) FontWeight.Bold
                                                     else null,
                                        color = if (it == filterVal)
                                                     MaterialTheme.colorScheme.primary
                                                else Color.Unspecified,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Divider(
                                        modifier = Modifier.fillMaxWidth().height(1.dp)
                                    )
                                }
                            }
                        }
                    }
                } /** End StudentSection ****/

            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(roleVal, filterVal) },
                       modifier = Modifier.focusRequester(confirmFocusReq)) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
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