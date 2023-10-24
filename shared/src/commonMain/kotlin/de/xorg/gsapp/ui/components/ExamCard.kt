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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.ui.materialtools.MaterialColors
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn


@Composable
fun ExamCard(
    exam: Exam,
    modifier: Modifier = Modifier
) {
    val harmonizedColor = MaterialColors.harmonize(
        colorToHarmonize = (exam.subject?.color ?: MaterialTheme.colorScheme.tertiaryContainer).toArgb(),
        colorToHarmonizeWith = MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(color = harmonizedColor,
        isLightTheme = isSystemInDarkTheme()
    )

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp,
            vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            SuggestionChip(
                onClick = {},
                label = {
                    Text(text = exam.label,
                         style = MaterialTheme.typography.titleMedium) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(colorRoles.accentContainer),
                    labelColor = Color(colorRoles.onAccentContainer)
                ),
                modifier = Modifier.alignByBaseline()
            )

            Box(modifier = Modifier.weight(1f).alignByBaseline()) {
                Text(text = (exam.subject?.longName ?: exam.label.filter { it.isLetter() }) + " " +
                        exam.label.filter { it.isDigit() } + " " +
                        (if (exam.isCoursework) "Kursarbeit" else "Klausur"),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.titleLarge)
            }

            Text(text = exam.date.todayUntilString())
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
