package de.xorg.gsapp.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import de.xorg.gsapp.data.push.DesktopPushUtilStub
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.sql.GsAppDatabase
import org.koin.dsl.module
import java.io.File
import java.util.prefs.Preferences

actual val platformModule = module {
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

    single<PushNotificationUtil> {
        DesktopPushUtilStub()
    }
}