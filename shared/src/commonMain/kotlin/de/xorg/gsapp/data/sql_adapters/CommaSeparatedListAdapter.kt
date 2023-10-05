package de.xorg.gsapp.data.sql_adapters

import app.cash.sqldelight.ColumnAdapter

object CommaSeparatedListAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) listOf()
        else databaseValue.split(",")
    override fun encode(value: List<String>) = value.joinToString(separator = ",")
}