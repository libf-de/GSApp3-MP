package de.xorg.gsapp.`data`

import androidx.compose.ui.graphics.Color
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public class DbSubstitutionQueries(
  driver: SqlDriver,
  private val DbSubstitutionSetAdapter: DbSubstitutionSet.Adapter,
  private val DbSubjectAdapter: DbSubject.Adapter,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: Long,
    assSet: Long,
    klass: String,
    klassFilter: String,
    lessonNr: String?,
    origSubject: String?,
    substTeacher: String?,
    substRoom: String?,
    substSubject: String?,
    notes: String?,
    isNew: Boolean,
  ) -> T): Query<T> = Query(-1_840_242_342, arrayOf("DbSubstitution"), driver, "DbSubstitution.sq",
      "selectAll", """
  |SELECT *
  |FROM DbSubstitution
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getString(7),
      cursor.getString(8),
      cursor.getString(9),
      cursor.getBoolean(10)!!
    )
  }

  public fun selectAll(): Query<DbSubstitution> = selectAll { id, assSet, klass, klassFilter,
      lessonNr, origSubject, substTeacher, substRoom, substSubject, notes, isNew ->
    DbSubstitution(
      id,
      assSet,
      klass,
      klassFilter,
      lessonNr,
      origSubject,
      substTeacher,
      substRoom,
      substSubject,
      notes,
      isNew
    )
  }

  public fun <T : Any> selectBySetId(id: Long, mapper: (
    id: Long,
    assSet: Long,
    klass: String,
    klassFilter: String,
    lessonNr: String?,
    origSubject: String?,
    substTeacher: String?,
    substRoom: String?,
    substSubject: String?,
    notes: String?,
    isNew: Boolean,
  ) -> T): Query<T> = SelectBySetIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getString(7),
      cursor.getString(8),
      cursor.getString(9),
      cursor.getBoolean(10)!!
    )
  }

  public fun selectBySetId(id: Long): Query<DbSubstitution> = selectBySetId(id) { id_, assSet,
      klass, klassFilter, lessonNr, origSubject, substTeacher, substRoom, substSubject, notes,
      isNew ->
    DbSubstitution(
      id_,
      assSet,
      klass,
      klassFilter,
      lessonNr,
      origSubject,
      substTeacher,
      substRoom,
      substSubject,
      notes,
      isNew
    )
  }

  public fun <T : Any> findSubstitutionsBySetId(id: Long, mapper: (
    id: Long,
    assSet: Long,
    klass: String,
    klassFilter: String,
    lessonNr: String?,
    origShortName: String?,
    origLongName: String?,
    origColor: Color?,
    substTeacherShortName: String?,
    substTeacherLongName: String?,
    substRoom: String?,
    substShortName: String?,
    substLongName: String?,
    substColor: Color?,
    notes: String?,
    isNew: Boolean,
  ) -> T): Query<T> = FindSubstitutionsBySetIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getLong(7)?.let { DbSubjectAdapter.colorAdapter.decode(it) },
      cursor.getString(8),
      cursor.getString(9),
      cursor.getString(10),
      cursor.getString(11),
      cursor.getString(12),
      cursor.getLong(13)?.let { DbSubjectAdapter.colorAdapter.decode(it) },
      cursor.getString(14),
      cursor.getBoolean(15)!!
    )
  }

  public fun findSubstitutionsBySetId(id: Long): Query<FindSubstitutionsBySetId> =
      findSubstitutionsBySetId(id) { id_, assSet, klass, klassFilter, lessonNr, origShortName,
      origLongName, origColor, substTeacherShortName, substTeacherLongName, substRoom,
      substShortName, substLongName, substColor, notes, isNew ->
    FindSubstitutionsBySetId(
      id_,
      assSet,
      klass,
      klassFilter,
      lessonNr,
      origShortName,
      origLongName,
      origColor,
      substTeacherShortName,
      substTeacherLongName,
      substRoom,
      substShortName,
      substLongName,
      substColor,
      notes,
      isNew
    )
  }

  public fun <T : Any> selectByDate(date: LocalDate, mapper: (
    id: Long,
    assSet: Long,
    klass: String,
    klassFilter: String,
    lessonNr: String?,
    origSubject: String?,
    substTeacher: String?,
    substRoom: String?,
    substSubject: String?,
    notes: String?,
    isNew: Boolean,
    id_: Long,
    dateStr: String,
    date: LocalDate,
    notes_: String?,
    hashCode: Long,
  ) -> T): Query<T> = SelectByDateQuery(date) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6),
      cursor.getString(7),
      cursor.getString(8),
      cursor.getString(9),
      cursor.getBoolean(10)!!,
      cursor.getLong(11)!!,
      cursor.getString(12)!!,
      DbSubstitutionSetAdapter.dateAdapter.decode(cursor.getLong(13)!!),
      cursor.getString(14),
      cursor.getLong(15)!!
    )
  }

  public fun selectByDate(date: LocalDate): Query<SelectByDate> = selectByDate(date) { id, assSet,
      klass, klassFilter, lessonNr, origSubject, substTeacher, substRoom, substSubject, notes,
      isNew, id_, dateStr, date_, notes_, hashCode ->
    SelectByDate(
      id,
      assSet,
      klass,
      klassFilter,
      lessonNr,
      origSubject,
      substTeacher,
      substRoom,
      substSubject,
      notes,
      isNew,
      id_,
      dateStr,
      date_,
      notes_,
      hashCode
    )
  }

  public fun deleteBySetId(id: Long) {
    driver.execute(-757_279_056, """
        |DELETE FROM DbSubstitution
        |WHERE assSet = ?
        """.trimMargin(), 1) {
          bindLong(0, id)
        }
    notifyQueries(-757_279_056) { emit ->
      emit("DbSubstitution")
    }
  }

  public fun insertSubstitution(
    assSet: Long,
    klass: String,
    klassFilter: String,
    lessonNr: String?,
    origSubject: String?,
    substTeacher: String?,
    substRoom: String?,
    substSubject: String?,
    notes: String?,
    isNew: Boolean,
  ) {
    driver.execute(1_473_238_433, """
        |INSERT OR IGNORE INTO DbSubstitution(assSet, klass, klassFilter, lessonNr, origSubject, substTeacher, substRoom, substSubject, notes, isNew)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 10) {
          bindLong(0, assSet)
          bindString(1, klass)
          bindString(2, klassFilter)
          bindString(3, lessonNr)
          bindString(4, origSubject)
          bindString(5, substTeacher)
          bindString(6, substRoom)
          bindString(7, substSubject)
          bindString(8, notes)
          bindBoolean(9, isNew)
        }
    notifyQueries(1_473_238_433) { emit ->
      emit("DbSubstitution")
    }
  }

  public fun insertSubstitutionWithStrings(
    date: LocalDate,
    klass: String,
    klassFilter: String,
    lessonNr: String?,
    shortName: String,
    shortName_: String,
    substRoom: String?,
    shortName__: String,
    notes: String?,
    isNew: Boolean,
  ) {
    driver.execute(-947_522_949, """
        |INSERT INTO DbSubstitution(assSet, klass, klassFilter, lessonNr, origSubject, substTeacher, substRoom, substSubject, notes, isNew)
        |VALUES ((SELECT id FROM DbSubstitutionSet WHERE date = ?), ?, ?, ?, (SELECT shortName FROM DbSubject WHERE shortName = ? COLLATE NOCASE), (SELECT shortName FROM DbTeacher WHERE shortName = ? COLLATE NOCASE), ?, (SELECT shortName FROM DbSubject WHERE shortName = ? COLLATE NOCASE), ?, ?)
        """.trimMargin(), 10) {
          bindLong(0, DbSubstitutionSetAdapter.dateAdapter.encode(date))
          bindString(1, klass)
          bindString(2, klassFilter)
          bindString(3, lessonNr)
          bindString(4, shortName)
          bindString(5, shortName_)
          bindString(6, substRoom)
          bindString(7, shortName__)
          bindString(8, notes)
          bindBoolean(9, isNew)
        }
    notifyQueries(-947_522_949) { emit ->
      emit("DbSubstitution")
    }
  }

  private inner class SelectBySetIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbSubstitution", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbSubstitution", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(170_949_183, """
    |SELECT *
    |FROM DbSubstitution
    |WHERE assSet = ?
    """.trimMargin(), mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "DbSubstitution.sq:selectBySetId"
  }

  private inner class FindSubstitutionsBySetIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbSubstitution", "DbSubject", "DbTeacher", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbSubstitution", "DbSubject", "DbTeacher", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_827_636_596, """
    |SELECT DbSubstitution.id,
    |        DbSubstitution.assSet,
    |        DbSubstitution.klass,
    |        DbSubstitution.klassFilter,
    |        DbSubstitution.lessonNr,
    |        DbSubstitution.origSubject AS origShortName, oSub.longName AS origLongName, oSub.color AS origColor,
    |        DbSubstitution.substTeacher AS substTeacherShortName, DbTeacher.longName AS substTeacherLongName,
    |        DbSubstitution.substRoom,
    |        DbSubstitution.substSubject AS substShortName, sSub.longName AS substLongName, sSub.color AS substColor,
    |        DbSubstitution.notes,
    |        DbSubstitution.isNew
    |FROM DbSubstitution
    |LEFT JOIN DbSubject oSub ON oSub.shortName = DbSubstitution.origSubject
    |LEFT JOIN DbSubject sSub ON sSub.shortName = DbSubstitution.substSubject
    |LEFT JOIN DbTeacher ON DbSubstitution.substTeacher = DbTeacher.shortName
    |WHERE assSet == ? COLLATE NOCASE
    """.trimMargin(), mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "DbSubstitution.sq:findSubstitutionsBySetId"
  }

  private inner class SelectByDateQuery<out T : Any>(
    public val date: LocalDate,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbSubstitution", "DbSubstitutionSet", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbSubstitution", "DbSubstitutionSet", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-1_657_504_180, """
    |SELECT *
    |FROM DbSubstitution
    |JOIN DbSubstitutionSet ON DbSubstitution.assSet = DbSubstitutionSet.id
    |WHERE DbSubstitutionSet.date = ?
    """.trimMargin(), mapper, 1) {
      bindLong(0, DbSubstitutionSetAdapter.dateAdapter.encode(date))
    }

    override fun toString(): String = "DbSubstitution.sq:selectByDate"
  }
}
