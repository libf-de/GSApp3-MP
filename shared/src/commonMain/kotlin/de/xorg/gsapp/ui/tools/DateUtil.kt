/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
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

package de.xorg.gsapp.ui.tools

import androidx.compose.runtime.Composable
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate

/**
 * Helper functions to work with localized dates.
 */
class DateUtil {
    companion object {
        @Composable
        fun getDateAsString(inp: LocalDate): String {
            return stringResource(MR.strings.date_format)
                .replace("d", inp.dayOfMonth.toString())
                .replace("m", inp.monthNumber.toString())
                .replace("y", inp.year.toString())
        }

        fun getDateAsString(inp: LocalDate, converter: (StringResource) -> String) {
            converter(MR.strings.date_format)
                .replace("d", inp.dayOfMonth.toString())
                .replace("m", inp.monthNumber.toString())
                .replace("y", inp.year.toString())
        }

        @Composable
        fun getWeekdayShort(inp: LocalDate): String {
            return stringResource(when(inp.dayOfWeek.ordinal) {
                0 -> MR.strings.wd_mo_sh
                1 -> MR.strings.wd_tu_sh
                2 -> MR.strings.wd_we_sh
                3 -> MR.strings.wd_th_sh
                4 -> MR.strings.wd_fr_sh
                5 -> MR.strings.wd_sa_sh
                else -> MR.strings.wd_su_sh
            })
        }

        @Composable
        fun getWeekdayLong(inp: LocalDate): String {
            return stringResource(getWeekdayLongRes(inp))
        }

        fun getWeekdayLongRes(inp: LocalDate): StringResource {
            return when(inp.dayOfWeek.ordinal) {
                0 -> MR.strings.wd_mo_lo
                1 -> MR.strings.wd_tu_lo
                2 -> MR.strings.wd_we_lo
                3 -> MR.strings.wd_th_lo
                4 -> MR.strings.wd_fr_lo
                5 -> MR.strings.wd_sa_lo
                else -> MR.strings.wd_su_lo
            }
        }

    }
}