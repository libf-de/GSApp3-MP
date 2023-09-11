package de.xorg.gsapp.data.sources.local

import org.kodein.di.DI
import org.kodein.di.DIAware

expect class PathSource(di: DI) : DIAware {
    fun getSubstitutionPath(): String
    fun getSubjectsPath(): String
    fun getTeachersPath(): String
    fun getFoodplanPath(): String
    fun getAdditivesPath(): String
}