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

package de.xorg.gsapp.data.model

/**
 * Data class to hold a single substitution plan entry, as fetched from the website
 * Subjects and teachers are still represented as strings, which should be matched
 * up with objects from the database and converted to a Substitution afterwards.
 * @property klass the affected class (spelled with k, to not interfere with kotlin class)
 * @property lessonNr
 * @property origSubject the subject to be substituted
 * @property substTeacher the teacher who replaces
 * @property substRoom the replacement room
 * @property substSubject the replacement subject
 * @property notes
 * @property isNew whether this is a new entry, as denoted by bold text on website
 */

data class SubstitutionApiModel(
    val klass: String,
    val lessonNr: String,
    val origSubject: String,
    val substTeacher: String,
    val substRoom: String,
    val substSubject: String,
    val notes: String,
    val isNew: Boolean
)
