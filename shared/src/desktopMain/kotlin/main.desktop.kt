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

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql.GsAppDatabase
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import java.io.File
import java.util.prefs.Preferences

actual fun getPlatformName(): String = "Desktop"

@OptIn(ExperimentalSettingsApi::class)
@Composable fun MainView() = withDI({
    bind<FlowSettings>() with singleton { PreferencesSettings(
        Preferences.userRoot()
    ).toFlowSettings() }
    bind<SqlDriver>() with singleton {
        val dbPath = "gsapp.db"

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath}")
        if(!File(dbPath).exists())
            GsAppDatabase.Schema.create(driver)
        driver
    }
    import(mainModule)
}) {
    GSApp()
}