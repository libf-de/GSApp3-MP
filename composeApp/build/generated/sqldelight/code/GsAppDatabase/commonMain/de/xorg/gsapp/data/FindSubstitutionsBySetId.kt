package de.xorg.gsapp.`data`

import androidx.compose.ui.graphics.Color
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class FindSubstitutionsBySetId(
  public val id: Long,
  public val assSet: Long,
  public val klass: String,
  public val klassFilter: String,
  public val lessonNr: String?,
  public val origShortName: String?,
  public val origLongName: String?,
  public val origColor: Color?,
  public val substTeacherShortName: String?,
  public val substTeacherLongName: String?,
  public val substRoom: String?,
  public val substShortName: String?,
  public val substLongName: String?,
  public val substColor: Color?,
  public val notes: String?,
  public val isNew: Boolean,
)
