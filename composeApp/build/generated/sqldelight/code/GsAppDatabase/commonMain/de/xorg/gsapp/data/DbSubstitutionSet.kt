package de.xorg.gsapp.`data`

import app.cash.sqldelight.ColumnAdapter
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public data class DbSubstitutionSet(
  public val id: Long,
  public val dateStr: String,
  public val date: LocalDate,
  public val notes: String?,
  public val hashCode: Long,
) {
  public class Adapter(
    public val dateAdapter: ColumnAdapter<LocalDate, Long>,
  )
}
