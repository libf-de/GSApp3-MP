package de.xorg.gsapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.materialtools.ColorRoles
import dev.icerock.moko.resources.FontResource
import dev.icerock.moko.resources.compose.fontFamilyResource

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
