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

package de.xorg.gsapp.data.enums

import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.course_eleven
import gsapp.composeapp.generated.resources.course_twelve
import gsapp.composeapp.generated.resources.examplan_course_eleven
import gsapp.composeapp.generated.resources.examplan_course_twelve
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalResourceApi::class)
enum class ExamCourse : ExamEnum {
    COURSE_11 {

        override val iconResource = Res.drawable.course_eleven
        override val descriptiveResource = Res.string.examplan_course_eleven
    },

    COURSE_12
    {
        override val iconResource = Res.drawable.course_twelve
        override val descriptiveResource = Res.string.examplan_course_twelve
    };

    companion object {
        val default = COURSE_11
    }
}

@OptIn(ExperimentalResourceApi::class)
private interface ExamEnum {
    val iconResource: DrawableResource
    val descriptiveResource: StringResource
}