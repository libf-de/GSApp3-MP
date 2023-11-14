package de.xorg.gsapp.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.datetime.LocalDate

public class DbFoodQueries(
  driver: SqlDriver,
  private val DbFoodAdapter: DbFood.Adapter,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    date: LocalDate,
    foodId: Long,
    name: String,
    additives: List<String>,
  ) -> T): Query<T> = Query(1_512_652_185, arrayOf("DbFood"), driver, "DbFood.sq", "selectAll", """
  |SELECT *
  |FROM DbFood
  """.trimMargin()) { cursor ->
    mapper(
      DbFoodAdapter.dateAdapter.decode(cursor.getLong(0)!!),
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      DbFoodAdapter.additivesAdapter.decode(cursor.getString(3)!!)
    )
  }

  public fun selectAll(): Query<DbFood> = selectAll { date, foodId, name, additives ->
    DbFood(
      date,
      foodId,
      name,
      additives
    )
  }

  public fun <T : Any> selectByDate(date: LocalDate, mapper: (
    date: LocalDate,
    foodId: Long,
    name: String,
    additives: List<String>,
  ) -> T): Query<T> = SelectByDateQuery(date) { cursor ->
    mapper(
      DbFoodAdapter.dateAdapter.decode(cursor.getLong(0)!!),
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      DbFoodAdapter.additivesAdapter.decode(cursor.getString(3)!!)
    )
  }

  public fun selectByDate(date: LocalDate): Query<DbFood> = selectByDate(date) { date_, foodId,
      name, additives ->
    DbFood(
      date_,
      foodId,
      name,
      additives
    )
  }

  public fun selectAllDates(): Query<LocalDate> = Query(-1_206_488_980, arrayOf("DbFood"), driver,
      "DbFood.sq", "selectAllDates", """
  |SELECT DISTINCT date
  |FROM DbFood
  """.trimMargin()) { cursor ->
    DbFoodAdapter.dateAdapter.decode(cursor.getLong(0)!!)
  }

  public fun <T : Any> selectLatestFoods(mapper: (
    date: LocalDate,
    foodId: Long,
    name: String,
    additives: List<String>,
  ) -> T): Query<T> = Query(513_140_870, arrayOf("DbFood"), driver, "DbFood.sq",
      "selectLatestFoods", """
  |SELECT *
  |FROM DbFood
  |WHERE date = (
  |    SELECT date FROM DbFood ORDER BY date DESC LIMIT 1
  |)
  """.trimMargin()) { cursor ->
    mapper(
      DbFoodAdapter.dateAdapter.decode(cursor.getLong(0)!!),
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      DbFoodAdapter.additivesAdapter.decode(cursor.getString(3)!!)
    )
  }

  public fun selectLatestFoods(): Query<DbFood> = selectLatestFoods { date, foodId, name,
      additives ->
    DbFood(
      date,
      foodId,
      name,
      additives
    )
  }

  public fun insert(
    date: LocalDate,
    foodId: Long,
    name: String,
    additives: List<String>,
  ) {
    driver.execute(-1_919_383_387, """
        |INSERT OR IGNORE INTO DbFood(date, foodId, name, additives)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindLong(0, DbFoodAdapter.dateAdapter.encode(date))
          bindLong(1, foodId)
          bindString(2, name)
          bindString(3, DbFoodAdapter.additivesAdapter.encode(additives))
        }
    notifyQueries(-1_919_383_387) { emit ->
      emit("DbFood")
    }
  }

  public fun clearOld(olderThan: LocalDate) {
    driver.execute(-1_737_966_714, """DELETE FROM DbFood WHERE date < ?""", 1) {
          bindLong(0, DbFoodAdapter.dateAdapter.encode(olderThan))
        }
    notifyQueries(-1_737_966_714) { emit ->
      emit("DbFood")
    }
  }

  private inner class SelectByDateQuery<out T : Any>(
    public val date: LocalDate,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("DbFood", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("DbFood", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(663_913_901, """
    |SELECT *
    |FROM DbFood
    |WHERE date = ?
    """.trimMargin(), mapper, 1) {
      bindLong(0, DbFoodAdapter.dateAdapter.encode(date))
    }

    override fun toString(): String = "DbFood.sq:selectByDate"
  }
}
