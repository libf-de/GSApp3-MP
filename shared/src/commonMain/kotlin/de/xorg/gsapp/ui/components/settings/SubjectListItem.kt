package de.xorg.gsapp.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.ui.materialtools.MaterialColors
import de.xorg.gsapp.ui.tools.foregroundColorForBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListItem(
    modifier: Modifier = Modifier,
    subject: Subject,
    onColorClick: (sub: Subject) -> Unit,
    onNameEdited: (sub: Subject, newName: String) -> Unit,
    onDelete: (sub: Subject) -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editValue by rememberSaveable { mutableStateOf(subject.longName) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    /*Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {*/
            TextField(
                value = editValue,
                onValueChange = {value: String -> editValue = value},
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onNameEdited(subject, editValue)
                        isEditing = false
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.onKeyEvent {
                    when (it.key) {
                        Key.Enter -> {
                            onNameEdited(subject, editValue)
                            isEditing = false
                            focusManager.clearFocus()
                            true
                        }
                        Key.Escape -> {
                            editValue = subject.longName
                            focusManager.clearFocus()
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }.onFocusChanged {
                    if(!it.isFocused && isEditing)
                        onNameEdited(subject, editValue)
                    isEditing = it.isFocused
                }.focusRequester(focusRequester)
                    .fillMaxWidth()
                 /*.weight(1f)
                    .padding(8.dp)*/,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                leadingIcon = {
                    Box(contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .padding(start = 5.dp, //TODO: guidelines?
                                top = 5.dp,
                                bottom = 5.dp,
                                end = 10.dp)) {

                        /* LessonNumber -> Circle Background */
                        Box(contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(subject.color)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    onClick = { onColorClick(subject) },
                                    indication = rememberRipple(bounded = true))
                        ) {
                            Text(text = subject.shortName,
                                style = MaterialTheme.typography.titleSmall,
                                color = foregroundColorForBackground(subject.color),
                                overflow = TextOverflow.Clip,
                                modifier = Modifier.padding(bottom = 1.5f.dp))
                        }
                    }
                },
                trailingIcon = {
                    Row {
                        AnimatedVisibility(
                            visible = isEditing,
                            enter = fadeIn(tween(200)),
                            exit = fadeOut(tween(200))
                        ) {
                            IconButton(onClick = {
                                onNameEdited(subject, editValue)
                                isEditing = false
                                focusManager.clearFocus()
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = "",
                                    tint = Color(
                                        MaterialColors.harmonize(
                                            Color.Green.toArgb(),
                                            MaterialTheme.colorScheme.onPrimaryContainer.toArgb()
                                        )
                                    )
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isEditing,
                            enter = fadeIn(tween(200)),
                            exit = fadeOut(tween(200))
                        ) {
                            IconButton(onClick = {
                                editValue = subject.longName
                                isEditing = false
                                focusManager.clearFocus()
                            }) {
                                Icon(imageVector = Icons.Rounded.Clear,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        IconButton(onClick = { onDelete(subject) } ) {
                            Icon(Icons.Rounded.Delete, null)
                        }
                    }
                }
            )/*
        }

        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )
    }*/
}