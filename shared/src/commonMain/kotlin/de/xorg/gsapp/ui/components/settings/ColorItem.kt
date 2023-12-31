/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.ui.tools.foregroundColorForBackground

@Composable
fun ColorItem(
    color: Color,
    onClick: (Color) -> Unit,
    isSelected: Boolean = false,
    colorName: String? = null,
    modifier: Modifier = Modifier
) {
    val mod = if(isSelected)
                modifier.border(2.dp, foregroundColorForBackground(color), CircleShape)
                        .padding(1.dp)
              else modifier
    Box(contentAlignment = Alignment.Center,
        modifier = mod
            .clip(CircleShape)
            .background(color)
            .clickable(interactionSource = remember { MutableInteractionSource() },
                onClick = { onClick(color) },
                indication = rememberRipple(bounded = true)
            )
    ) {
        if(colorName != null)
            Text(text = colorName,
                style = MaterialTheme.typography.titleSmall,
                color = contentColorFor(color),
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(bottom = 1.5f.dp))
    }
}