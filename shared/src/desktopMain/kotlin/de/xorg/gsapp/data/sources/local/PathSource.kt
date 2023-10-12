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

package de.xorg.gsapp.data.sources.local

import net.harawata.appdirs.AppDirsFactory
import org.kodein.di.DI
import org.kodein.di.DIAware

actual class PathSource actual constructor(override val di: DI) : DIAware {

    val appDir: String = AppDirsFactory.getInstance().getUserCacheDir("gsapp", "3", "de.xorg")

    actual fun getSubstitutionPath(): String {
        return "$appDir/substitutions.json"
    }

    actual fun getSubjectsPath(): String {
        return "$appDir/subjects.json"
    }

    actual fun getTeachersPath(): String {
        return "$appDir/teachers.json"
    }

    actual fun getFoodplanPath(): String {
        return "$appDir/foodplan.json"
    }

    actual fun getAdditivesPath(): String {
        return "$appDir/additives.json"
    }
}