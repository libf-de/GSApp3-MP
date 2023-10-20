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

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.cache.AndroidCacheManager
import de.xorg.gsapp.data.cache.CacheManager
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql.GsAppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(ctx: Activity) = withDI({
    bind<SqlDriver>() with singleton {
        AndroidSqliteDriver(GsAppDatabase.Schema, ctx, "gsapp.db")
    }
    bind<Activity>() with provider { ctx }
    bind<CacheManager>() with singleton { AndroidCacheManager(ctx) }
    bind<DataStore<Preferences>>() with singleton {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(ctx, "GSApp")),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { ctx.preferencesDataStoreFile("GSApp") }
        )
    }
    bind<FlowSettings>() with singleton { DataStoreSettings(instance()) }
    import(mainModule)
}) {
    GSApp()
}
