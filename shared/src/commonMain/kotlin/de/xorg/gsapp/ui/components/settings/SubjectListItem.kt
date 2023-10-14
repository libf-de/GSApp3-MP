package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Subject

@Composable
fun SubjectListItem(
    modifier: Modifier = Modifier,
    subject: Subject,
    onClick: () -> Unit,
    onDelete: (sub: Subject) -> Unit
) {
    Column(
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
            indication = rememberRipple(bounded = true)
        )
    ) {
        Row {
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 15.dp,
                        top = 15.dp,
                        bottom = 15.dp)
                    .fillMaxHeight() ) {

                /* LessonNumber -> Circle Background */
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(subject.color)
                ) {
                    Text(text = subject.shortName,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColorFor(subject.color),
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.padding(bottom = 1.5f.dp))
                }
            }

            Text(
                text = subject.longName,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = { onDelete(subject) } ) {
                Icon(Icons.Rounded.Delete, null)
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )
    }
}