package de.xorg.gsapp.`data`

import app.cash.sqldelight.ColumnAdapter
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.datetime.LocalDate

public data class DbFood(
  public val date: LocalDate,
  public val foodId: Long,
  public val name: String,
  public val additives: List<String>,
) {
  public class Adapter(
    public val dateAdapter: ColumnAdapter<LocalDate, Long>,
    public val additivesAdapter: ColumnAdapter<List<String>, String>,
  )
}
