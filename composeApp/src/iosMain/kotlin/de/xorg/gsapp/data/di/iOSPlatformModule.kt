package de.xorg.gsapp.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.push.iosPushUtilStub
import de.xorg.gsapp.data.sources.remote.GsWebsiteParser
import de.xorg.gsapp.data.sources.remote.IosWebsiteParser
import de.xorg.gsapp.data.sql.GsAppDatabase
import de.xorg.gsapp.ui.tools.IOSPlatformImpl
import de.xorg.gsapp.ui.tools.PlatformInterface
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDefaults.Companion.standardUserDefaults

@OptIn(ExperimentalSettingsApi::class)
val iosModule = module {
    single<PlatformInterface> { IOSPlatformImpl() }

    single<SqlDriver> {
        NativeSqliteDriver(GsAppDatabase.Schema, "gsapp.db")
    }

    single {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults()).toFlowSettings()
    }

    single<PushNotificationUtil> {
        iosPushUtilStub()
    }

    single<GsWebsiteParser> {
        IosWebsiteParser()
    }
}