package de.xorg.gsapp.data.sql_adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDate

object DateAdapter : ColumnAdapter<LocalDate, Long> {
    override fun decode(databaseValue: Long): LocalDate = LocalDate.fromEpochDays(databaseValue.toInt())

    override fun encode(value: LocalDate): Long = value.toEpochDays().toLong()
}