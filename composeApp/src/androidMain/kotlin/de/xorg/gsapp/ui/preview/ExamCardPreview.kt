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