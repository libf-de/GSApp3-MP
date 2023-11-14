package de.xorg.gsapp.`data`

import androidx.compose.ui.graphics.Color
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import de.xorg.gsapp.`data`.enums.ExamCourse
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.datetime.LocalDate

public class DbExamQueries(
  driver: SqlDriver,
  private val DbExamAdapter: DbExam.Adapter,
  private val DbSubjectAdapter: DbSubject.Adapter,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: Long,
    label: String,
    date: LocalDate,
    course: ExamCourse,
    isCoursework: Boolean,
    subject: String?,
  ) -> T): Query<T> = Query(-26_446_984, arrayOf("DbExam"), driver, "DbExam.sq", "selectAll", """
  |SELECT *
  |FROM DbExam
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      DbExamAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      DbExamAdapter.courseAdapter.decode(cursor.getString(3)!!),
      cursor.getBoolean(4)!!,
      cursor.getString(5)
    )
  }

  public fun selectAll(): Query<DbExam> = selectAll { id, label, date, course, isCoursework,
      subject ->
    DbExam(
      id,
      label,
      date,
      course,
      isCoursework,
      subject
    )
  }

  public fun <T : Any> selectAllWithSubjects(mapper: (
    id: Long,
    label: String,
    course: ExamCourse,
    date: LocalDate,
    isCoursework: Boolean,
    subject: String?,
    subjectLongName: String?,
    subjectColor: Color?,
  ) -> T): Query<T> = Query(-1_971_416_251, arrayOf("DbExam", "DbSubject"), driver, "DbExam.sq",
      "selectAllWithSubjects", """
  |SELECT DbExam.id, DbExam.label, DbExam.course, DbExam.date, DbExam.isCoursework, DbExam.subject,
  |       dSub.longName AS subjectLongName, dSub.color AS subjectColor
  |FROM DbExam
  |LEFT JOIN DbSubject dSub ON DbExam.subject = dSub.shortName COLLATE NOCASE
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      DbExamAdapter.courseAdapter.decode(cursor.getString(2)!!),
      DbExamAdapter.dateAdapter.decode(cursor.getLong(3)!!),
      cursor.getBoolean(4)!!,
      cursor.getString(5),
      cursor.getString(6),
      cursor.getLong(7)?.let { DbSubjectAdapter.colorAdapter.decode(it) }
    )
  }

  public fun selectAllWithSubjects(): Query<SelectAllWithSubjects> = selectAllWithSubjects { id,
      label, course, date, isCoursework, subject, subjectLongName, subjectColor ->
    SelectAllWithSubjects(
      id,
      label,
      course,
      date,
      isCoursework,
      subject,
      subjectLongName,
      subjectColor
    )
  }

  public fun <T : Any> selectByCourseWithSubjects(course: ExamCourse, mapper: (
    id: Long,
    label: String,
    course: ExamCourse,
    date: LocalDate,
    isCoursework: Boolean,
    subject: String?,
    subjectLongName: String?,
    subjectColor: Color?,
  ) -> T): Query<T> = SelectByCourseWithSubjectsQuery(course) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      DbExamAdapter.courseAdapter.decode(cursor.getString(2)!!),
      DbExamAdapter.dateAdapter.decode(cursor.getLong(3)!!),
      cursor.getBoolean(4)!!,
      cursor.getString(5),
      cursor.getString(6),
      cursor.getLong(7)?.let { DbSubjectAdapter.colorAdapter.decode(it) }
    )
  }

  public fun selectByCourseWithSubjects(course: ExamCourse): Query<SelectByCourseWithSubjects> =
      selectByCourseWithSubjects(course) { id, label, course_, date, isCoursework, subject,
      subjectLongName, subjectColor ->
    SelectByCourseWithSubjects(
      id,
      label,
      course_,
      date,
      isCoursework,
      subject,
      subjectLongName,
      subjectColor
    )
  }

  public fun clearAll() {
    driver.execute(-402_155_199, """DELETE FROM DbExam""", 0)
    notifyQueries(-402_155_199) { emit ->
      emit("DbExam")
    }
  }

  public fun clearAllFromCouse(course: ExamCourse) {
    driver.execute(-1_781_603_376, """
        |DELETE FROM DbExam
        |WHERE course = ?
        """.trimMargin(), 1) {
          bindString(0, DbExamAdapter.courseAdapter.encode(course))
        }
    notifyQueries(-1_781_603_376) { emit ->
      emit("DbExam")
    }
  }

  public fun insert(
    label: String,
    date: LocalDate,
    course: ExamCourse,
    isCoursework: Boolean,
    subject: String?,
  ) {
    driver.execute(-559_335_642, """
        |INSERT OR IGNORE INTO DbExam(label, date, course, isCoursework, subject)
        |VALUES (?, ?, ?, ?, ?)
        """.trimMargin(), 5) {
          bindString(0, label)
          bindLong(1, DbExamAdapter.dateAdapter.encode(date))
          bindString(2, DbExamAdapter.courseAdapter.encode(course))
          bindBoolean(3, isCoursework)
          bindString(4, subject)
        }
    notifyQueries(-559_335_642) { emit ->
      emit("DbExam")
    }
  }

  public fun delete(id: Long) {
    driver.execute(-711_001_576, """
        |DELETE FROM DbExam
        |WHERE id = ?
        """.trimMargin(), 1) {
          bindLong(0, id)
        }
    notifyQueries(-711_001_576) { emit ->
      emit("DbExam")
    }
  }

  public fun deleteByValues(label: String, date: LocalDate) {
    driver.execute(1_939_439_249, """
        |DELETE FROM DbExam
        |WHERE label = ?
        |AND date = ?
        """.trimMargin(), 2) {
          bindString(0, label)
          bindLong(1, DbExamAdapter.dateAdapter.encode(date))
        }
    notifyQueries(1_939_439_249) { emit ->
      emit("DbExam")
    }
  }

  public fun clearOlder(limitDate: LocalDate) {
    driver.execute(88_835_252, """
        |DELETE FROM DbExam
        |WHERE date < ?
        """.trimMargin(), 1) {
          bindLong(0, DbExamAdapter.dateAdapter.encode(limitDate))
        }
    notifyQueries(88_835_252) { emit ->
      emit("DbExam")
    }
  }

  private inner class SelectByCourseWithSubjectsQuery<out T : Any>(
    public val course: ExamCourse,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbExam", "DbSubject", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbExam", "DbSubject", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(2_081_108_776, """
    |SELECT DbExam.id, DbExam.label, DbExam.course, DbExam.date, DbExam.isCoursework, DbExam.subject,
    |       dSub.longName AS subjectLongName, dSub.color AS subjectColor
    |FROM DbExam
    |LEFT JOIN DbSubject dSub ON DbExam.subject = dSub.shortName
    |WHERE DbExam.course = ? COLLATE NOCASE
    """.trimMargin(), mapper, 1) {
      bindString(0, DbExamAdapter.courseAdapter.encode(course))
    }

    override fun toString(): String = "DbExam.sq:selectByCourseWithSubjects"
  }
}
