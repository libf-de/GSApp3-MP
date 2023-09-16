package de.xorg.gsapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import de.xorg.gsapp.ui.colortools.MaterialColors
import de.xorg.gsapp.ui.colortools.utilities.ColorUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FoodplanCard(
    food: Food,
    color: Color
) {
    val harmonizedColor = MaterialColors.harmonize(
        color.toArgb(),
        MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(harmonizedColor, isSystemInDarkTheme())

    var currentRotation by remember { mutableStateOf(180f) }
    val rotation = remember { Animatable(currentRotation) }

    var expanded by rememberSaveable { // Save it because it will scroll of screen
        mutableStateOf(true)
    }

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
        modifier = Modifier.padding(4.dp).clickable {
            expanded = !expanded
        }.wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(colorRoles.accentContainer))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp, 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(modifier = Modifier,
                        style = MaterialTheme.typography.titleSmall,
                        text = "${food.num}. Menü",
                        color = Color(colorRoles.onAccentContainer))
                    Text(modifier = Modifier,
                        style = MaterialTheme.typography.titleLarge,
                        text = food.name,
                        color = Color(colorRoles.onAccentContainer)
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    tint = Color(colorRoles.accent),
                    contentDescription = "",
                    modifier = Modifier.rotate(rotation.value).clickable {
                        expanded = !expanded
                    }
                )
            }
            if(expanded) {
                Divider(color = Color(colorRoles.accent), modifier = Modifier)
                Text(text = food.additives.joinToString(", "),
                     modifier = Modifier.padding(start = 12.dp,
                                                 end = 12.dp,
                                                 top = 4.dp,
                                                 bottom = 6.dp),
                     color = Color(colorRoles.onAccentContainer)
                ) //12h 6v
                /*FlowRow(modifier = Modifier) {
                    food.additives.forEach {
                        AssistChip(
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = Color(colorRoles.accent)
                            ),
                            onClick = {},
                            label = {
                                Text(it)
                            }
                        )
                    }
                }*/
            }
        }
        Box() {


        }

    }

}