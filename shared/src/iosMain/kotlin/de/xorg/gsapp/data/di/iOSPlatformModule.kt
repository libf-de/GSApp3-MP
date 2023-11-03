package de.xorg.gsapp.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.push.iosPushUtilStub
import de.xorg.gsapp.data.sql.GsAppDatabase
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults.Companion.standardUserDefaults

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule = module {
    single<SqlDriver> {
        NativeSqliteDriver(GsAppDatabase.Schema, "gsapp.db")
    }

    single {
        NSUserDefaultsSettings(standardUserDefaults()).toFlowSettings()
    }

    single<PushNotificationUtil> {
        iosPushUtilStub()
    }
}