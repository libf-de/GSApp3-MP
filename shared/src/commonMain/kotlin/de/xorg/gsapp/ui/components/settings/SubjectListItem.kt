package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.ui.materialtools.MaterialColors
import de.xorg.gsapp.ui.state.FilterRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListItem(
    modifier: Modifier = Modifier,
    subject: Subject,
    onColorClick: (sub: Subject) -> Unit,
    onNameEdited: (sub: Subject, newName: String) -> Unit,
    onDelete: (sub: Subject) -> Unit
) {
    var longEditShow by remember { mutableStateOf(false) }
    var longEditValue by remember { mutableStateOf(subject.longName) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 5.dp, //TODO: guidelines?
                        top = 5.dp,
                        bottom = 5.dp)) {

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
                        color = contentColorFor(subject.color),
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.padding(bottom = 1.5f.dp))
                }
            }

            TextField(
                value = longEditValue,
                onValueChange = {value: String -> longEditValue = value},
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onNameEdited(subject, longEditValue)
                        longEditShow = false
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.onKeyEvent {
                    when (it.key) {
                        Key.Enter -> {
                            onNameEdited(subject, longEditValue)
                            longEditShow = false
                            focusManager.clearFocus()
                            true
                        }
                        Key.Escape -> {
                            longEditValue = subject.longName
                            focusManager.clearFocus()
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }.onFocusChanged {
                    if(!it.isFocused && longEditShow)
                        onNameEdited(subject, longEditValue)
                    longEditShow = it.isFocused
                }.focusRequester(focusRequester)
                 .weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                trailingIcon = {
                    if(longEditShow) {
                        Row {
                            //val harmonizedColor = MaterialColors.harmonize(
                            //        color.toArgb(),
                            //        MaterialTheme.colorScheme.primary.toArgb())

                            IconButton(onClick = {
                                onNameEdited(subject, longEditValue)
                                longEditShow = false
                                focusManager.clearFocus()
                            }) {
                                Icon(imageVector = Icons.Rounded.Done,
                                    contentDescription = "",
                                    tint = Color(MaterialColors.harmonize(Color.Green.toArgb(),
                                        MaterialTheme.colorScheme.onPrimaryContainer.toArgb())))
                            }

                            IconButton(onClick = {
                                longEditValue = subject.longName
                                longEditShow = false
                                focusManager.clearFocus()
                            }) {
                                Icon(imageVector = Icons.Rounded.Clear,
                                     contentDescription = "",
                                     tint = Color(MaterialColors.harmonize(Color.Red.toArgb(),
                                         MaterialTheme.colorScheme.onPrimaryContainer.toArgb())))
                            }

                        }

                    }
                }
            )

            Text(subject.longName)

            IconButton(onClick = { onDelete(subject) } ) {
                Icon(Icons.Rounded.Delete, null)
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )
    }
}