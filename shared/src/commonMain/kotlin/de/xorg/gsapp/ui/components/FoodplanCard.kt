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

package de.xorg.gsapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.ui.materialtools.MaterialColors
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.stringResource

/**
 * A card that displays a single food in the foodplan,
 * showing the menu number, the name of the food and, expandable,
 * the list of additives.
 */
@Composable
fun FoodplanCard(
    food: Food,
    menuNumber: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Harmonize the meal/card color with (system) primary color
    val harmonizedColor = MaterialColors.harmonize(
        color.toArgb(),
        MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(harmonizedColor, isSystemInDarkTheme())

    // Remember-Variables used for expanding the card, and rotating the expand-arrow
    var currentRotation by remember { mutableStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        if (expanded) {
            rotation.animateTo(
                targetValue = currentRotation + 180f
            ) {
                currentRotation = value
            }
        } else {
            rotation.animateTo(
                targetValue = 0f
            ) {
                currentRotation = value
            }
        }
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable { expanded = !expanded }
            .wrapContentHeight()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color(colorRoles.accentContainer))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp, 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(modifier = Modifier,
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(MR.strings.foodplan_menu_no, menuNumber), //1. Menü
                        color = Color(colorRoles.onAccentContainer))
                    Text(
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge, //Spaghetti Bolognese
                        text = food.name,
                        color = Color(colorRoles.onAccentContainer)
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    tint = Color(colorRoles.accent),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(rotation.value).clickable {
                        expanded = !expanded
                    }
                )
            }
            if(expanded) {
                Divider(color = Color(colorRoles.accent), modifier = Modifier)
                Text(text = food.additives.sorted().joinToString(", "),
                     modifier = Modifier.padding(start = 12.dp,
                                                 end = 12.dp,
                                                 top = 4.dp,
                                                 bottom = 6.dp),
                     color = Color(colorRoles.onAccentContainer),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

}