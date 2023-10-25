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

package de.xorg.gsapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import de.xorg.gsapp.data.di.preferencesRepositoryModule
import de.xorg.gsapp.data.di.sqldelightLocalSourceModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

class GSAppApplication : Application(), DIAware {
    companion object {
        lateinit var instance: GSAppApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override val di: DI = DI.lazy {
        bind<Context>() with provider { applicationContext }
        bind<DataStore<Preferences>>() with singleton {
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                migrations = listOf(SharedPreferencesMigration(applicationContext, "GSApp")),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { applicationContext.preferencesDataStoreFile("GSApp") }
            )
        }
        bind<FlowSettings>() with singleton { DataStoreSettings(instance()) }
        import(preferencesRepositoryModule)
    }
}