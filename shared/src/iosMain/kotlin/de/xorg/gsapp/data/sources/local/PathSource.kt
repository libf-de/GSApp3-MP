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

import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import org.kodein.di.DI
import org.kodein.di.DIAware
import platform.Foundation.NSArray
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.arrayWithObjects
import platform.Foundation.pathWithComponents

actual class PathSource actual constructor(override val di: DI) : DIAware {

    private val paths =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)

    @OptIn(ExperimentalForeignApi::class)
    fun ensureDir() {
        val cacheDir = NSString.pathWithComponents(
            NSArray.arrayWithObjects(
                paths.first().toString(),
                "gsapp".cstr
            )
        )
        val cBooleanPointer: CPointer<BooleanVar> = nativeHeap.alloc(true).ptr
        val isExistent = NSFileManager.defaultManager.fileExistsAtPath(
            path = cacheDir,
            isDirectory = cBooleanPointer
        )

        if(!isExistent && !cBooleanPointer.pointed.value) {
            NSFileManager.defaultManager.createDirectoryAtPath(
                cacheDir,
                mapOf(Pair("withIntermediateDirectories", true))
            )
        }

    }

    fun getPath(fileName: String): String {
        return NSString.pathWithComponents(
            NSArray.arrayWithObjects(
                paths.first().toString() as NSString,
                "gsapp" as NSString,
                fileName as NSString
            )
        )
    }

    actual fun getSubstitutionPath(): String {
        //return "${paths.first().toString()}/gsapp/substitutions.json"
        return "${NSHomeDirectory()}/substitutions.json"
    }

    actual fun getSubjectsPath(): String {
        //return "${paths.first().toString()}/gsapp/subjects.json"
        return "${NSHomeDirectory()}/subjects.json"
    }

    actual fun getTeachersPath(): String {
        //return "${paths.first().toString()}/gsapp/teachers.json"
        return "${NSHomeDirectory()}/teachers.json"
    }

    actual fun getFoodplanPath(): String {
        //return "${paths.first().toString()}/gsapp/foodplan.json"
        return "${NSHomeDirectory()}/foodplan.json"
    }

    actual fun getAdditivesPath(): String {
        //return "${paths.first().toString()}/gsapp/additives.json"
        return "${NSHomeDirectory()}/additives.json"
    }
}