package de.xorg.gsapp.`data`

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.`data`.enums.ExamCourse
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public data class SelectByCourseWithSubjects(
  public val id: Long,
  public val label: String,
  public val course: ExamCourse,
  public val date: LocalDate,
  public val isCoursework: Boolean,
  public val subject: String?,
  public val subjectLongName: String?,
  public val subjectColor: Color?,
)
