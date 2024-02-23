package de.xorg.gsapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.ui.materialtools.ColorRoles

@Composable
fun EncircledText(
    text: @Composable () -> Unit,
    colorRoles: ColorRoles,
    isSingleLine: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = Alignment.CenterStart,
        modifier = modifier ) {

        /* LessonNumber -> Circle Background */
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(colorRoles.accent))
        ) {
            text()
        }
    }
}
