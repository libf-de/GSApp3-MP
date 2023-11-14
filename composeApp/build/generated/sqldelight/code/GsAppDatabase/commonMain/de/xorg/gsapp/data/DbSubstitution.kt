package de.xorg.gsapp.`data`

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class DbSubstitution(
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
)
