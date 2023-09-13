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

package de.xorg.gsapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SubstitutionDisplay(
    val type: SubstitutionType,
    val klass: String,
    val lessonNr: String,
    val origSubject: Subject,
    val substTeacher: Teacher,
    val substRoom: String,
    val substSubject: Subject,
    val notes: String,
    val isNew: Boolean
) {

    constructor(primitive: Substitution,
                origSubject: Subject,
                substTeacher: Teacher,
                substSubject: Subject) : this(
                    type = if(primitive.notes.lowercase() == "ausfall")
                        SubstitutionType.CANCELLATION
                    else if(primitive.notes.lowercase() == "aa"
                            || primitive.notes.lowercase().startsWith("arbeitsauftr"))
                        SubstitutionType.WORKORDER
                    else if(primitive.notes.lowercase() == "raumtausch")
                        SubstitutionType.ROOMSWAP
                    else if(primitive.notes.lowercase().startsWith("stillbesch"))
                        SubstitutionType.BREASTFEED
                    else
                        SubstitutionType.NORMAL,
                    klass = primitive.klass.ifBlank { "(kein)" },
                    lessonNr = primitive.lessonNr.ifBlank { "?" },
                    origSubject = origSubject,
                    substTeacher = substTeacher,
                    substRoom = primitive.substRoom.ifBlank { "(kein)" },
                    substSubject = substSubject,
                    notes = primitive.notes,
                    isNew = primitive.isNew
                ) {

    }
}
