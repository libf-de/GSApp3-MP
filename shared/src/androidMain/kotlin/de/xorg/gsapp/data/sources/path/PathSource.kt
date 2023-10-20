package de.xorg.gsapp.data.sources.path

import de.xorg.gsapp.data.cache.CacheManager
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import java.io.File

actual class PathSource actual constructor(override val di: DI) : DIAware {

    private val cacheManager by di.instance<CacheManager>()

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