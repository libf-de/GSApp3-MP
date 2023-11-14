package de.xorg.gsapp.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import de.xorg.gsapp.data.push.DesktopPushUtilStub
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.sources.remote.GsWebsiteParser
import de.xorg.gsapp.data.sources.remote.JavaWebsiteParser
import de.xorg.gsapp.data.sql.GsAppDatabase
import de.xorg.gsapp.ui.tools.PlatformInterface
import org.koin.dsl.module
import platform.DesktopPlatformImpl
import java.io.File
import java.util.prefs.Preferences

@OptIn(ExperimentalSettingsApi::class)
val desktopModule = module {
    single<PlatformInterface> { DesktopPlatformImpl() }

    single<SqlDriver> {
        val dbPath = "gsapp.db"

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath}")
        if(!File(dbPath).exists())
            GsAppDatabase.Schema.create(driver)
        driver
    }

    single {
        PreferencesSettings(Preferences.userRoot()).toFlowSettings()
    }

    single<GsWebsiteParser> {
        JavaWebsiteParser()
    }

    single<PushNotificationUtil> {
        DesktopPushUtilStub()
    }
}