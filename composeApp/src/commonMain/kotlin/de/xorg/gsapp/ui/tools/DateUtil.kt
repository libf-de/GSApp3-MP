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
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.date_format
import gsapp.composeapp.generated.resources.wd_fr_lo
import gsapp.composeapp.generated.resources.wd_fr_sh
import gsapp.composeapp.generated.resources.wd_mo_lo
import gsapp.composeapp.generated.resources.wd_mo_sh
import gsapp.composeapp.generated.resources.wd_sa_lo
import gsapp.composeapp.generated.resources.wd_sa_sh
import gsapp.composeapp.generated.resources.wd_su_lo
import gsapp.composeapp.generated.resources.wd_su_sh
import gsapp.composeapp.generated.resources.wd_th_lo
import gsapp.composeapp.generated.resources.wd_th_sh
import gsapp.composeapp.generated.resources.wd_tu_lo
import gsapp.composeapp.generated.resources.wd_tu_sh
import gsapp.composeapp.generated.resources.wd_we_lo
import gsapp.composeapp.generated.resources.wd_we_sh
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Helper functions to work with localized dates.
 */
@OptIn(ExperimentalResourceApi::class)
class DateUtil {
    companion object {
        @Composable
        fun getDateAsString(inp: LocalDate): String {
            return stringResource(Res.string.date_format)
                .replace("d", inp.dayOfMonth.toString())
                .replace("m", inp.monthNumber.toString())
                .replace("y", inp.year.toString())
        }

        fun getDateAsString(inp: LocalDate, converter: (StringResource) -> String) {
            converter(Res.string.date_format)
                .replace("d", inp.dayOfMonth.toString())
                .replace("m", inp.monthNumber.toString())
                .replace("y", inp.year.toString())
        }


        @Composable
        fun getWeekdayShort(inp: LocalDate): String {
            return stringResource(when(inp.dayOfWeek.ordinal) {
                0 -> Res.string.wd_mo_sh
                1 -> Res.string.wd_tu_sh
                2 -> Res.string.wd_we_sh
                3 -> Res.string.wd_th_sh
                4 -> Res.string.wd_fr_sh
                5 -> Res.string.wd_sa_sh
                else -> Res.string.wd_su_sh
            })
        }

        @Composable
        fun getWeekdayLong(inp: LocalDate): String {
            return stringResource(getWeekdayLongRes(inp))
        }

        fun getWeekdayLongRes(inp: LocalDate): StringResource {
            return when(inp.dayOfWeek.ordinal) {
                0 -> Res.string.wd_mo_lo
                1 -> Res.string.wd_tu_lo
                2 -> Res.string.wd_we_lo
                3 -> Res.string.wd_th_lo
                4 -> Res.string.wd_fr_lo
                5 -> Res.string.wd_sa_lo
                else -> Res.string.wd_su_lo
            }
        }



    }
}