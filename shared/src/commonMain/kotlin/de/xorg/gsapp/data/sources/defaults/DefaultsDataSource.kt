package de.xorg.gsapp.data.sources.defaults

import de.xorg.gsapp.data.model.Subject

interface DefaultsDataSource {
    fun getDefaultSubjects(): List<Subject>
}