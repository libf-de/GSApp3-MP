package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    icon: @Composable (modifier: Modifier, tint: Color) -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }, // TODO: Can this be done better?
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))

        icon(
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
        Spacer(modifier = Modifier.width(24.dp))
    }
}