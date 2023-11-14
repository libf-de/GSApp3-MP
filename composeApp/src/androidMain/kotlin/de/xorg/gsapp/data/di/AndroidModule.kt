package de.xorg.gsapp.data.di

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import de.xorg.gsapp.data.push.AndroidPushUtil
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.sources.remote.GsWebsiteParser
import de.xorg.gsapp.data.sources.remote.JavaWebsiteParser
import de.xorg.gsapp.data.sql.GsAppDatabase
import de.xorg.gsapp.ui.tools.AndroidPlatformImpl
import de.xorg.gsapp.ui.tools.PlatformInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val androidModule = module {
    single<PlatformInterface> { AndroidPlatformImpl() }

    single<SqlDriver> {
        AndroidSqliteDriver(GsAppDatabase.Schema, get(), "gsapp.db")
    }

    single {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(get(), "GSApp")),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { get<Context>().preferencesDataStoreFile("GSApp") }
        )
    }

    single<GsWebsiteParser> {
        JavaWebsiteParser()
    }

    single<FlowSettings> {
        DataStoreSettings(get())
    }

    single<PushNotificationUtil> {
        AndroidPushUtil()
    }
}