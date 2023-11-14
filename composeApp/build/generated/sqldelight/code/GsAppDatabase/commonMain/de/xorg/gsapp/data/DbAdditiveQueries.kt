package de.xorg.gsapp.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.String

public class DbAdditiveQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (shortName: String, longName: String) -> T): Query<T> =
      Query(1_184_694_507, arrayOf("DbAdditive"), driver, "DbAdditive.sq", "selectAll", """
  |SELECT *
  |FROM DbAdditive
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!
    )
  }

  public fun selectAll(): Query<DbAdditive> = selectAll { shortName, longName ->
    DbAdditive(
      shortName,
      longName
    )
  }

  public fun insert(shortName: String, longName: String) {
    driver.execute(-1_175_333_229, """
        |INSERT OR IGNORE INTO DbAdditive(shortName, longName)
        |VALUES (?, ?)
        """.trimMargin(), 2) {
          bindString(0, shortName)
          bindString(1, longName)
        }
    notifyQueries(-1_175_333_229) { emit ->
      emit("DbAdditive")
    }
  }

  public fun deleteByShort(short: String) {
    driver.execute(-811_674_336, """DELETE FROM DbAdditive WHERE shortName = ?""", 1) {
          bindString(0, short)
        }
    notifyQueries(-811_674_336) { emit ->
      emit("DbAdditive")
    }
  }
}
