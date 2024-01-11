package de.xorg.gsapp.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.ui.materialtools.MaterialColors

@Composable
fun ExamChip(
    exam: Exam,
    modifier: Modifier = Modifier
) {
    val subjectTitle = remember(key1 = exam.hashCode()) {
        exam.subject?.longName ?: exam.label.filter { it.isLetter() }
    }

    val subcourses = remember(key1 = exam.hashCode()) {
        exam.label
            .filter { it.isDigit() }
            .map { it.toString() }
            .joinToString(", ")
    }

    val harmonizedColor = MaterialColors.harmonize(
        colorToHarmonize = (exam.subject?.color ?: MaterialTheme.colorScheme.tertiaryContainer).toArgb(),
        colorToHarmonizeWith = MaterialTheme.colorScheme.primary.toArgb())
    val colorRoles = MaterialColors.getColorRoles(color = harmonizedColor,
        isLightTheme = !isSystemInDarkTheme()
    )


    SuggestionChip(
        label = {
            Text(
                text = "$subjectTitle $subcourses",
                fontWeight = if(exam.isCoursework) FontWeight.Black else null
            )
        },
        onClick = { },
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = Color(colorRoles.accentContainer),
            labelColor = Color(colorRoles.onAccentContainer)
        ),
        border = if(exam.isCoursework)
            SuggestionChipDefaults.suggestionChipBorder(
                borderColor = Color(colorRoles.onAccentContainer),
                borderWidth = 4.dp
            )
        else
            SuggestionChipDefaults.suggestionChipBorder(
                borderColor = Color(colorRoles.onAccentContainer)
            )
    )
}