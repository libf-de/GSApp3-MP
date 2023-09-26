package de.xorg.gsapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.ui.tools.shimmerBackground

@Composable
fun ClassListItem(
    label: String,
    highlight: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
            indication = rememberRipple(bounded = true)
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(12.dp),
            fontWeight = if (highlight) FontWeight.Bold else null,
            color = if (highlight) MaterialTheme.colorScheme.primary else Color.Unspecified,
            style = MaterialTheme.typography.bodyLarge
        )
        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )
    }
}

@Composable
fun SkeletonClassListItem(
    modifier: Modifier = Modifier
) {
    val skelHeight = with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp()}
    Column {
        Box(modifier = modifier
            .padding(12.dp)
            .size(width=64.dp, height=skelHeight)
            .shimmerBackground(isSystemInDarkTheme(),  RoundedCornerShape(2.dp)),)
        Divider(
            modifier = Modifier.fillMaxWidth().height(1.dp)
        )
    }

}