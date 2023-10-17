package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.ui.state.FilterRole

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

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 10.dp,
                        top = 10.dp,
                        bottom = 10.dp)) {

                /* LessonNumber -> Circle Background */
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
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

            if(longEditShow) {
                TextField(
                    value = longEditValue,
                    onValueChange = {value -> longEditValue = value},
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onNameEdited(subject, longEditValue)
                            longEditShow = false
                        }
                    ),
                    modifier = Modifier.onKeyEvent {
                        if (it.key == Key.Enter){
                            onNameEdited(subject, longEditValue)
                            longEditShow = false
                            true
                        }
                        false
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            onNameEdited(subject, longEditValue)
                            longEditShow = false
                        }) {
                            Icon(Icons.Rounded.Done, "")
                        }
                    }
                )
            } else {
                Text(
                    text = subject.longName,
                    modifier = Modifier.padding(12.dp).clickable {
                        longEditShow = true
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = { onDelete(subject) } ) {
                Icon(Icons.Rounded.Delete, null)
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )
    }
}