package de.xorg.gsapp.`data`

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public class DbSubstitutionSetQueries(
  driver: SqlDriver,
  private val DbSubstitutionSetAdapter: DbSubstitutionSet.Adapter,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: Long,
    dateStr: String,
    date: LocalDate,
    notes: String?,
    hashCode: Long,
  ) -> T): Query<T> = Query(-2_012_833_038, arrayOf("DbSubstitutionSet"), driver,
      "DbSubstitutionSet.sq", "selectAll", """
  |SELECT *
  |FROM DbSubstitutionSet
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      DbSubstitutionSetAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getString(3),
      cursor.getLong(4)!!
    )
  }

  public fun selectAll(): Query<DbSubstitutionSet> = selectAll { id, dateStr, date, notes,
      hashCode ->
    DbSubstitutionSet(
      id,
      dateStr,
      date,
      notes,
      hashCode
    )
  }

  public fun <T : Any> selectLatest(mapper: (
    id: Long,
    dateStr: String,
    date: LocalDate,
    notes: String?,
    hashCode: Long,
  ) -> T): Query<T> = Query(-1_965_514_602, arrayOf("DbSubstitutionSet"), driver,
      "DbSubstitutionSet.sq", "selectLatest", """
  |SELECT *
  |FROM DbSubstitutionSet
  |ORDER BY id DESC
  |LIMIT 1
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      DbSubstitutionSetAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getString(3),
      cursor.getLong(4)!!
    )
  }

  public fun selectLatest(): Query<DbSubstitutionSet> = selectLatest { id, dateStr, date, notes,
      hashCode ->
    DbSubstitutionSet(
      id,
      dateStr,
      date,
      notes,
      hashCode
    )
  }

  public fun getIdByDateString(dateStr: String): Query<Long> = GetIdByDateStringQuery(dateStr) {
      cursor ->
    cursor.getLong(0)!!
  }

  public fun lastInsertRowId(): ExecutableQuery<Long> = Query(-1_230_511_341, driver,
      "DbSubstitutionSet.sq", "lastInsertRowId", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  public fun getLegacyIds(olderThan: LocalDate): Query<Long> = GetLegacyIdsQuery(olderThan) {
      cursor ->
    cursor.getLong(0)!!
  }

  public fun insertSubstitutionSet(
    dateStr: String,
    date: LocalDate,
    notes: String?,
    hashCode: Long,
  ) {
    driver.execute(1_669_551_257, """
        |INSERT INTO DbSubstitutionSet(dateStr, date, notes, hashCode)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindString(0, dateStr)
          bindLong(1, DbSubstitutionSetAdapter.dateAdapter.encode(date))
          bindString(2, notes)
          bindLong(3, hashCode)
        }
    notifyQueries(1_669_551_257) { emit ->
      emit("DbSubstitutionSet")
    }
  }

  public fun deleteSubstitutionSet(id: Long) {
    driver.execute(-906_686_617, """DELETE FROM DbSubstitutionSet WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(-906_686_617) { emit ->
      emit("DbSubstitutionSet")
    }
  }

  private inner class GetIdByDateStringQuery<out T : Any>(
    public val dateStr: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbSubstitutionSet", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbSubstitutionSet", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_029_322_316, """
    |SELECT id
    |FROM DbSubstitutionSet
    |WHERE dateStr = ?
    """.trimMargin(), mapper, 1) {
      bindString(0, dateStr)
    }

    override fun toString(): String = "DbSubstitutionSet.sq:getIdByDateString"
  }

  private inner class GetLegacyIdsQuery<out T : Any>(
    public val olderThan: LocalDate,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbSubstitutionSet", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbSubstitutionSet", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_708_738_452, """
    |SELECT id FROM DbSubstitutionSet
    |WHERE date < ?
    """.trimMargin(), mapper, 1) {
      bindLong(0, DbSubstitutionSetAdapter.dateAdapter.encode(olderThan))
    }

    override fun toString(): String = "DbSubstitutionSet.sq:getLegacyIds"
  }
}
