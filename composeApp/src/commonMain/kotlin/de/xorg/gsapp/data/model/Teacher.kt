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
 * Data class to represent a teacher. Could probably be replaced with a map, but will keep this,
 * maybe will add gender later to display teacher in substitutions like "Hr. Mustermann" instead of
 * "Mustermann, Max". The problem is that the website does not specify gender, so will have to use
 * lookup table or https://genderize.io or https://gender-api.com
 * @param shortName short name, as displayed in substitution plan on website
 * @param longName long name that will be shown to the user.
 */
data class Teacher(
    val shortName: String,
    val longName: String
) {
    /**
     * Constructor that constructs a "dummy" teacher if a shortName is not in the database.
     * Will display the shortName to the user.
     */
    constructor(shortName: String) : this(shortName, shortName)
}