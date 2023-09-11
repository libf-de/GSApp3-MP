package de.xorg.gsapp.data.sources.local

import net.harawata.appdirs.AppDirsFactory

actual class PathSource {

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