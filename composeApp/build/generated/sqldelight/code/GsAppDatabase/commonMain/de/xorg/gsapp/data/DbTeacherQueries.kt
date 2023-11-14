package de.xorg.gsapp.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.String

public class DbTeacherQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (shortName: String, longName: String) -> T): Query<T> =
      Query(1_630_400_085, arrayOf("DbTeacher"), driver, "DbTeacher.sq", "selectAll",
      "SELECT * FROM DbTeacher") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!
    )
  }

  public fun selectAll(): Query<DbTeacher> = selectAll { shortName, longName ->
    DbTeacher(
      shortName,
      longName
    )
  }

  public fun <T : Any> selectByShort(shortName: String, mapper: (shortName: String,
      longName: String) -> T): Query<T> = SelectByShortQuery(shortName) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!
    )
  }

  public fun selectByShort(shortName: String): Query<DbTeacher> = selectByShort(shortName) {
      shortName_, longName ->
    DbTeacher(
      shortName_,
      longName
    )
  }

  public fun selectLongFromShort(shortName: String): Query<String> =
      SelectLongFromShortQuery(shortName) { cursor ->
    cursor.getString(0)!!
  }

  public fun insertTeacher(shortName: String, longName: String) {
    driver.execute(-1_202_668_999, """
        |INSERT OR IGNORE INTO DbTeacher(shortName, longName)
        |VALUES (?, ?)
        """.trimMargin(), 2) {
          bindString(0, shortName)
          bindString(1, longName)
        }
    notifyQueries(-1_202_668_999) { emit ->
      emit("DbTeacher")
    }
  }

  public fun updateTeacher(longName: String, shortName: String) {
    driver.execute(2_107_855_401, """
        |UPDATE DbTeacher
        |SET longName = ?
        |WHERE shortName = ? COLLATE NOCASE
        """.trimMargin(), 2) {
          bindString(0, longName)
          bindString(1, shortName)
        }
    notifyQueries(2_107_855_401) { emit ->
      emit("DbTeacher")
    }
  }

  public fun deleteTeacher(shortName: String) {
    driver.execute(-895_933_689, """
        |DELETE FROM DbTeacher
        |WHERE shortName = ? COLLATE NOCASE
        """.trimMargin(), 1) {
          bindString(0, shortName)
        }
    notifyQueries(-895_933_689) { emit ->
      emit("DbTeacher")
    }
  }

  private inner class SelectByShortQuery<out T : Any>(
    public val shortName: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbTeacher", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbTeacher", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_796_907_289,
        """SELECT * FROM DbTeacher WHERE shortName = ? COLLATE NOCASE""", mapper, 1) {
      bindString(0, shortName)
    }

    override fun toString(): String = "DbTeacher.sq:selectByShort"
  }

  private inner class SelectLongFromShortQuery<out T : Any>(
    public val shortName: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbTeacher", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbTeacher", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-525_680_758,
        """SELECT longName FROM DbTeacher WHERE shortName = ? COLLATE NOCASE""", mapper, 1) {
      bindString(0, shortName)
    }

    override fun toString(): String = "DbTeacher.sq:selectLongFromShort"
  }
}
