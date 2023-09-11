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
import platform.Foundation.NSArray
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.arrayWithObjects
import platform.Foundation.pathWithComponents

actual class PathSource {

    private val paths =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true);

    @OptIn(ExperimentalForeignApi::class)
    fun ensureDir() {
        val cacheDir = NSString.pathWithComponents(
            NSArray.arrayWithObjects(
                paths.first().toString(),
                "gsapp".cstr
            )
        )
        val cBooleanPointer: CPointer<BooleanVar> = nativeHeap.alloc(true).ptr
        var isExistent = NSFileManager.defaultManager.fileExistsAtPath(
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

    @OptIn(ExperimentalForeignApi::class)
    fun getPath(fileName: String): String {
        return NSString.pathWithComponents(
            NSArray.arrayWithObjects(
                paths.first().toString(),
                "gsapp".cstr,
                fileName.cstr
            )
        )
    }

    actual fun getSubstitutionPath(): String {
        return getPath("substitutions.json")
    }

    actual fun getSubjectsPath(): String {
        return getPath("subjects.json")
    }

    actual fun getTeachersPath(): String {
        return getPath("teachers.json")
    }

    actual fun getFoodplanPath(): String {
        return getPath("foodplan.json")
    }

    actual fun getAdditivesPath(): String {
        return getPath("additives.json")
    }
}