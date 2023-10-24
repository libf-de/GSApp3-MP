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
import android.content.Context
import de.xorg.gsapp.data.cache.AndroidCacheManager
import de.xorg.gsapp.data.cache.CacheManager
import de.xorg.gsapp.data.di.mainModule
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bind
import org.kodein.di.provider
import org.kodein.di.singleton

class GSAppApplication : Application(), DIAware {
    companion object {
        lateinit var instance: GSAppApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override val di: DI = DI {
        bind<Context>() with provider { applicationContext }
        bind<CacheManager>() with singleton { AndroidCacheManager(applicationContext) }
        import(mainModule)
        import(androidXModule(this@GSAppApplication))
    }

    /*override val di: DI by DI.lazy {
        bind<SettingsSource>() with provider { SettingsSource(this@GSAppApplication) }
        bind<Context>() with provider { applicationContext }
        bind<CacheManager>() with singleton { AndroidCacheManager(applicationContext) }
        import(mainModule)
        import(androidXModule(this@GSAppApplication))
    }*/
}