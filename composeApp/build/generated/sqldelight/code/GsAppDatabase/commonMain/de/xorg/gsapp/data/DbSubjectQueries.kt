package de.xorg.gsapp.`data`

import androidx.compose.ui.graphics.Color
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class DbSubjectQueries(
  driver: SqlDriver,
  private val DbSubjectAdapter: DbSubject.Adapter,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    shortName: String,
    longName: String,
    color: Color?,
  ) -> T): Query<T> = Query(-1_195_881_141, arrayOf("DbSubject"), driver, "DbSubject.sq",
      "selectAll", """
  |SELECT *
  |FROM DbSubject
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)?.let { DbSubjectAdapter.colorAdapter.decode(it) }
    )
  }

  public fun selectAll(): Query<DbSubject> = selectAll { shortName, longName, color ->
    DbSubject(
      shortName,
      longName,
      color
    )
  }

  public fun countAll(): Query<Long> = Query(2_138_574_604, arrayOf("DbSubject"), driver,
      "DbSubject.sq", "countAll", """
  |SELECT COUNT()
  |FROM DbSubject
  """.trimMargin()) { cursor ->
    cursor.getLong(0)!!
  }

  public fun countByShort(shortName: String): Query<Long> = CountByShortQuery(shortName) { cursor ->
    cursor.getLong(0)!!
  }

  public fun insertSubject(
    shortName: String,
    longName: String,
    color: Color?,
  ) {
    driver.execute(1_535_063_929, """
        |INSERT OR IGNORE INTO DbSubject(shortName, longName, color)
        |VALUES (?, ?, ?)
        """.trimMargin(), 3) {
          bindString(0, shortName)
          bindString(1, longName)
          bindLong(2, color?.let { DbSubjectAdapter.colorAdapter.encode(it) })
        }
    notifyQueries(1_535_063_929) { emit ->
      emit("DbSubject")
    }
  }

  public fun updateSubject(
    longName: String,
    color: Color?,
    shortName: String,
  ) {
    driver.execute(550_621_033, """
        |UPDATE DbSubject
        |SET longName = ?, color = ?
        |WHERE shortName = ? COLLATE NOCASE
        """.trimMargin(), 3) {
          bindString(0, longName)
          bindLong(1, color?.let { DbSubjectAdapter.colorAdapter.encode(it) })
          bindString(2, shortName)
        }
    notifyQueries(550_621_033) { emit ->
      emit("DbSubject")
    }
  }

  public fun deleteSubject(shortName: String) {
    driver.execute(1_841_799_239, """
        |DELETE FROM DbSubject
        |WHERE shortName = ? COLLATE NOCASE
        """.trimMargin(), 1) {
          bindString(0, shortName)
        }
    notifyQueries(1_841_799_239) { emit ->
      emit("DbSubject")
    }
  }

  public fun deleteAllSubjects() {
    driver.execute(-1_208_401_117, """DELETE FROM DbSubject""", 0)
    notifyQueries(-1_208_401_117) { emit ->
      emit("DbSubject")
    }
  }

  private inner class CountByShortQuery<out T : Any>(
    public val shortName: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbSubject", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbSubject", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(560_434_768, """
    |SELECT COUNT()
    |FROM DbSubject
    |WHERE shortName = ? COLLATE NOCASE
    """.trimMargin(), mapper, 1) {
      bindString(0, shortName)
    }

    override fun toString(): String = "DbSubject.sq:countByShort"
  }
}
