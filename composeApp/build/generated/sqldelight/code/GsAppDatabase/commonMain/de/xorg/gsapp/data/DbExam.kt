package de.xorg.gsapp.`data`

import app.cash.sqldelight.ColumnAdapter
import de.xorg.gsapp.`data`.enums.ExamCourse
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public data class DbExam(
  public val id: Long,
  public val label: String,
  public val date: LocalDate,
  public val course: ExamCourse,
  public val isCoursework: Boolean,
  public val subject: String?,
) {
  public class Adapter(
    public val dateAdapter: ColumnAdapter<LocalDate, Long>,
    public val courseAdapter: ColumnAdapter<ExamCourse, String>,
  )
}
