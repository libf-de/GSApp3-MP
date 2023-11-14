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
import de.xorg.gsapp.data.di.appModule
import de.xorg.gsapp.data.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class GSAppApplication : Application() {
    companion object {
        lateinit var instance: GSAppApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GSAppApplication)
            androidLogger()
            modules(appModule() + androidModule)
        }

        instance = this
    }
}