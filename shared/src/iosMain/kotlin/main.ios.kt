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

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.moriatsushi.insetsx.WindowInsetsUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql.GsAppDatabase
import moe.tlaster.precompose.PreComposeApplication
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import platform.Foundation.NSUserDefaults.Companion.standardUserDefaults

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = WindowInsetsUIViewController { PreComposeApplication {
    withDI({
        bind<Settings>() with singleton { NSUserDefaultsSettings(standardUserDefaults()) }
        bind<SqlDriver>() with singleton {
            NativeSqliteDriver(GsAppDatabase.Schema, "gsapp.db")
        }

        //bind<SettingsSource>() with singleton { SettingsSource(NSObject()) }
        import(mainModule)
    }) { GSApp() }
} }
