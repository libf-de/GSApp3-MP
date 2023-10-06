package de.xorg.gsapp.data.model

import de.xorg.gsapp.data.DbExam
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
