package de.xorg.gsapp.`data`.sql

import app.cash.sqldelight.Transacter
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
import de.xorg.gsapp.`data`.sql.composeApp.newInstance
import de.xorg.gsapp.`data`.sql.composeApp.schema
import kotlin.Unit

public interface GsAppDatabase : Transacter {
  public val dbAdditiveQueries: DbAdditiveQueries

  public val dbExamQueries: DbExamQueries

  public val dbFoodQueries: DbFoodQueries

  public val dbSubjectQueries: DbSubjectQueries

  public val dbSubstitutionQueries: DbSubstitutionQueries

  public val dbSubstitutionSetQueries: DbSubstitutionSetQueries

  public val dbTeacherQueries: DbTeacherQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = GsAppDatabase::class.schema

    public operator fun invoke(
      driver: SqlDriver,
      DbExamAdapter: DbExam.Adapter,
      DbFoodAdapter: DbFood.Adapter,
      DbSubjectAdapter: DbSubject.Adapter,
      DbSubstitutionSetAdapter: DbSubstitutionSet.Adapter,
    ): GsAppDatabase = GsAppDatabase::class.newInstance(driver, DbExamAdapter, DbFoodAdapter,
        DbSubjectAdapter, DbSubstitutionSetAdapter)
  }
}
