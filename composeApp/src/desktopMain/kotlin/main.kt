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

import androidx.compose.ui.window.application
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.appModule
import de.xorg.gsapp.data.di.desktopModule
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.PreComposeWindow
import org.koin.core.context.startKoin

fun main() = application {
    PreComposeWindow(onCloseRequest = ::exitApplication, title = stringResource(MR.strings.app_name)) {
        startKoin {
            modules(appModule() + desktopModule)
        }

        GSApp()
    }
}