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

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import de.xorg.gsapp.res.MR

enum class ExamCourse : ExamEnum {
    COURSE_11 {
        override val iconResource = MR.images.course_eleven
        override val descriptiveResource = MR.strings.examplan_course_eleven
    },

    COURSE_12
    {
        override val iconResource = MR.images.course_twelve
        override val descriptiveResource = MR.strings.examplan_course_twelve
    };

    companion object {
        val default = COURSE_11
    }
}

private interface ExamEnum {
    val iconResource: ImageResource
    val descriptiveResource: StringResource
}