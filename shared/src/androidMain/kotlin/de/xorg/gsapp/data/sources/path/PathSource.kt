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

package de.xorg.gsapp.data.sources.path

import de.xorg.gsapp.data.cache.CacheManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

actual class PathSource : KoinComponent {

    private val cacheManager: CacheManager by inject()

    actual fun getSubstitutionPath(): String {
        return File(cacheManager.getCacheDirectory(), "substitutions.json").absolutePath
    }

    actual fun getSubjectsPath(): String {
        return File(cacheManager.getCacheDirectory(), "subjects.json").absolutePath
    }

    actual fun getTeachersPath(): String {
        return File(cacheManager.getCacheDirectory(), "teachers.json").absolutePath
    }

    actual fun getFoodplanPath(): String {
        return File(cacheManager.getCacheDirectory(), "foodplan.json").absolutePath
    }

    actual fun getAdditivesPath(): String {
        return File(cacheManager.getCacheDirectory(), "additives.json").absolutePath
    }
}