/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.data.sql_adapters

import app.cash.sqldelight.ColumnAdapter

/**
 * This is an adapter that enables storing Lists of Strings in sqldelight databases as comma-
 * separated strings. Only used for additives in Foodplan, certainly shouldn't be used with
 * user-provided strings which might contain commas.
 */
object CommaSeparatedListAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) listOf()
        else databaseValue.split(",")
    override fun encode(value: List<String>) = value.joinToString(separator = ",")
}