package de.xorg.gsapp.`data`

import androidx.compose.ui.graphics.Color
import app.cash.sqldelight.ColumnAdapter
import kotlin.Long
import kotlin.String

public data class DbSubject(
  public val shortName: String,
  public val longName: String,
  public val color: Color?,
) {
  public class Adapter(
    public val colorAdapter: ColumnAdapter<Color, Long>,
  )
}
