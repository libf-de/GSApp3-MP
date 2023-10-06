package de.xorg.gsapp.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.ui.components.ExamCard
import kotlinx.datetime.LocalDate

@Composable
@Preview
fun ExamCardPreview() {
    ExamCard(exam = Exam(
        "DE12",
        LocalDate(year = 2023, monthNumber = 11, dayOfMonth = 5),
        ExamCourse.COURSE_12,
        subject = Subject(
            "De", "Deutsch", Color.Blue
        )
    ))
}