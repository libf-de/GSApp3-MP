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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.ui.materialtools.MaterialColors
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.SairaCondensed_Medium
import gsapp.composeapp.generated.resources.SairaExtraCondensed_Medium
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font


@Composable
fun ExamCard(
    exam: Exam,
    modifier: Modifier = Modifier
) {
    val subjectTitle = remember {
        exam.subject?.longName ?: exam.label.filter { it.isLetter() }
    }

    val subcourses = remember {
        exam.label
            .filter { it.isDigit() }
            .map { it.toString() }
            .joinToString(", ")
    }

    val timeTillExam = remember {
        exam.date.todayUntilString()
    }


    val harmonizedColor = MaterialColors.harmonize(
        colorToHarmonize = (exam.subject?.color ?: MaterialTheme.colorScheme.tertiaryContainer).toArgb(),
        colorToHarmonizeWith = MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(color = harmonizedColor,
        isLightTheme = isSystemInDarkTheme()
    )

    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {

            //=== SubjectCircle ===
            EncircledText(
                text = {
                    AdaptiveText(
                        text = exam.label,
                        maxWidth = 38.dp,
                        defaultTextStyle = MaterialTheme.typography.titleMedium,
                        color = Color(colorRoles.onAccent),
                        modifier = Modifier/*.padding(bottom = 1.5f.dp)*/
                    )
                },
                colorRoles = colorRoles,
                modifier = Modifier.padding(end = 8.dp),
            )

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$subjectTitle $subcourses",
                    modifier = Modifier/*.padding(12.dp)*/,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(text = timeTillExam)
        }

    }
}

private fun LocalDate.todayUntilString(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysDiff = today.daysUntil(this)
    val weeksDiff = daysDiff / 7

    return if (daysDiff == 0)
                "heute"
           else if (daysDiff == 1)
                "morgen"
           else if (daysDiff > 7 && weeksDiff == 1)
                "in 1 Woche"
           else if (daysDiff > 7)
                "in $weeksDiff Wochen"
           else
                "in $daysDiff Tagen"
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AdaptiveText(
    text: String,
    maxWidth: Dp, // Maximale Breite in dp
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    defaultTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    SubcomposeLayout(modifier) { constraints ->
        val maxPx = maxWidth.toPx()

        // Subcompose und Messen des Textes mit der regulären Schriftart
        val regularText = subcompose("regular") {
            Text(
                text = text,
                style = defaultTextStyle,
                color = color,
                textAlign = TextAlign.Center,
                softWrap = false,
                modifier = Modifier
            )
        }.first().measure(constraints)

        // Layout-Strategie basierend auf der Textlänge
        layout(regularText.width, regularText.height) {
            if (regularText.width >= maxPx) {
                val condensedText = subcompose("condensed") {
                    Text(
                        text = text,
                        color = color,
                        style = defaultTextStyle.copy(
                            fontFamily = FontFamily(Font(Res.font.SairaCondensed_Medium)),
                        ),
                        textAlign = TextAlign.Center,
                        softWrap = false,
                        modifier = Modifier
                    )
                }.first().measure(constraints)

                if(condensedText.width >= maxPx) {
                    subcompose("extracondensed") {
                        Text(
                            text = text,
                            color = color,
                            style = defaultTextStyle.copy(
                                fontFamily = FontFamily(Font(Res.font.SairaExtraCondensed_Medium)),
                            ),
                            textAlign = TextAlign.Center,
                            softWrap = false,
                            modifier = Modifier
                        )
                    }.first().measure(constraints)
                } else {
                    condensedText
                }.let {
                    val x = (maxWidth.toPx() - it.width) / 2
                    it.place(x.toInt(), 0)
                }
            } else {
                // Platzieren des regulären Textes
                regularText.place(0, 0)
            }
        }
    }
}