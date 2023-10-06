/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
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

package de.xorg.gsapp.data.model

import de.xorg.gsapp.data.enums.ExamCourse
import kotlinx.datetime.LocalDate

/**
 * Exam data class
 * @property label -> Label, as displayed on website (DE12, GE, et1,…)
 * @property date -> self-explanatory
 * @property isCoursework -> whether it's coursework (true, -> "Leistungskurs", LABEL IN UPPERCASE)
 *                           or exam (false, -> "Grundkurs", label in lowercase)
 * @property subject -> the associated subject, used for color and long name in exam list
 */
data class Exam(
    val label: String,
    val date: LocalDate,
    val course: ExamCourse,
    val isCoursework: Boolean,
    val subject: Subject?) {

    /**
     * Exam data class, determines isCoursework using RegEx on label
     * @property label -> Label, as displayed on website (DE12, GE, et1,…)
     * @property date -> self-explanatory
     * @property subject -> the associated subject, used for color and long name in exam list
     */
    constructor(label: String, date: LocalDate, course: ExamCourse, subject: Subject?) :
            this(label = label,
                 date = date,
                 course  = course,
                 isCoursework = Regex("\"([A-Z]+(?!.*[0-9]))\"").matches(label),
                 subject = subject)

    constructor(label: String, date: LocalDate, course: ExamCourse) :
            this(label, date, course,null)
}
