package de.xorg.gsapp.data.sources.local

expect class PathSource() {
    fun getSubstitutionPath(): String
    fun getSubjectsPath(): String
    fun getTeachersPath(): String
    fun getFoodplanPath(): String
    fun getAdditivesPath(): String
}