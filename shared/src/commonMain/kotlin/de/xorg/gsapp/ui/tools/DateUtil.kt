package de.xorg.gsapp.ui.tools

import androidx.compose.runtime.Composable
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate

class DateUtil {
    companion object {
        @Composable
        fun getDateAsString(inp: LocalDate): String {
            return stringResource(MR.strings.date_format)
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
            return stringResource(when(inp.dayOfWeek.ordinal) {
                0 -> MR.strings.wd_mo_lo
                1 -> MR.strings.wd_tu_lo
                2 -> MR.strings.wd_we_lo
                3 -> MR.strings.wd_th_lo
                4 -> MR.strings.wd_fr_lo
                5 -> MR.strings.wd_sa_lo
                else -> MR.strings.wd_su_lo
            })
        }
    }
}