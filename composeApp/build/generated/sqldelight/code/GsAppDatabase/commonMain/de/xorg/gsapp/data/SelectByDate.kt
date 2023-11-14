package de.xorg.gsapp.`data`

import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public data class SelectByDate(
  public val id: Long,
  public val assSet: Long,
  public val klass: String,
  public val klassFilter: String,
  public val lessonNr: String?,
  public val origSubject: String?,
  public val substTeacher: String?,
  public val substRoom: String?,
  public val substSubject: String?,
  public val notes: String?,
  public val isNew: Boolean,
  public val id_: Long,
  public val dateStr: String,
  public val date: LocalDate,
  public val notes_: String?,
  public val hashCode: Long,
)
