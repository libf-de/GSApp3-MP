package de.xorg.gsapp.data.sources.local

import net.harawata.appdirs.AppDirsFactory
import org.kodein.di.DI
import org.kodein.di.DIAware
import java.util.prefs.Preferences

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