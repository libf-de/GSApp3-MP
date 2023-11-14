package de.xorg.gsapp.`data`.sql.composeApp

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import de.xorg.gsapp.`data`.DbAdditiveQueries
import de.xorg.gsapp.`data`.DbExam
import de.xorg.gsapp.`data`.DbExamQueries
import de.xorg.gsapp.`data`.DbFood
import de.xorg.gsapp.`data`.DbFoodQueries
import de.xorg.gsapp.`data`.DbSubject
import de.xorg.gsapp.`data`.DbSubjectQueries
import de.xorg.gsapp.`data`.DbSubstitutionQueries
import de.xorg.gsapp.`data`.DbSubstitutionSet
import de.xorg.gsapp.`data`.DbSubstitutionSetQueries
import de.xorg.gsapp.`data`.DbTeacherQueries
import de.xorg.gsapp.`data`.sql.GsAppDatabase
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<GsAppDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = GsAppDatabaseImpl.Schema

internal fun KClass<GsAppDatabase>.newInstance(
  driver: SqlDriver,
  DbExamAdapter: DbExam.Adapter,
  DbFoodAdapter: DbFood.Adapter,
  DbSubjectAdapter: DbSubject.Adapter,
  DbSubstitutionSetAdapter: DbSubstitutionSet.Adapter,
): GsAppDatabase = GsAppDatabaseImpl(driver, DbExamAdapter, DbFoodAdapter, DbSubjectAdapter,
    DbSubstitutionSetAdapter)

private class GsAppDatabaseImpl(
  driver: SqlDriver,
  DbExamAdapter: DbExam.Adapter,
  DbFoodAdapter: DbFood.Adapter,
  DbSubjectAdapter: DbSubject.Adapter,
  DbSubstitutionSetAdapter: DbSubstitutionSet.Adapter,
) : TransacterImpl(driver), GsAppDatabase {
  override val dbAdditiveQueries: DbAdditiveQueries = DbAdditiveQueries(driver)

  override val dbExamQueries: DbExamQueries = DbExamQueries(driver, DbExamAdapter, DbSubjectAdapter)

  override val dbFoodQueries: DbFoodQueries = DbFoodQueries(driver, DbFoodAdapter)

  override val dbSubjectQueries: DbSubjectQueries = DbSubjectQueries(driver, DbSubjectAdapter)

  override val dbSubstitutionQueries: DbSubstitutionQueries = DbSubstitutionQueries(driver,
      DbSubstitutionSetAdapter, DbSubjectAdapter)

  override val dbSubstitutionSetQueries: DbSubstitutionSetQueries = DbSubstitutionSetQueries(driver,
      DbSubstitutionSetAdapter)

  override val dbTeacherQueries: DbTeacherQueries = DbTeacherQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE DbAdditive (
          |    shortName TEXT NOT NULL PRIMARY KEY,
          |    longName TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE DbExam (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    label TEXT NOT NULL,
          |    date INTEGER NOT NULL,
          |    course TEXT NOT NULL,
          |    isCoursework INTEGER NOT NULL DEFAULT 0,
          |    subject TEXT,
          |    FOREIGN KEY(subject) REFERENCES DbSubject(shortName)
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE DbFood (
          |    date INTEGER NOT NULL,
          |    foodId INTEGER NOT NULL,
          |    name TEXT NOT NULL,
          |    additives TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE DbSubject (
          |    shortName TEXT NOT NULL PRIMARY KEY,
          |    longName TEXT NOT NULL,
          |    color INTEGER
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE DbSubstitution (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    assSet INTEGER NOT NULL,
          |    klass TEXT NOT NULL,
          |    klassFilter TEXT NOT NULL,
          |    lessonNr TEXT,
          |    origSubject TEXT,
          |    substTeacher TEXT,
          |    substRoom TEXT,
          |    substSubject TEXT,
          |    notes TEXT,
          |    isNew INTEGER NOT NULL DEFAULT 0,
          |    FOREIGN KEY(assSet) REFERENCES DbSubstitutionSet(id),
          |    FOREIGN KEY(substTeacher) REFERENCES DbTeacher(shortName),
          |    FOREIGN KEY(origSubject) REFERENCES DbSubject(shortName),
          |    FOREIGN KEY(substSubject) REFERENCES DbSubject(shortName))
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE DbSubstitutionSet (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    dateStr TEXT NOT NULL,
          |    date INTEGER NOT NULL,
          |    notes TEXT,
          |    hashCode INTEGER NOT NULL)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE DbTeacher (
          |    shortName TEXT NOT NULL PRIMARY KEY,
          |    longName TEXT NOT NULL)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
