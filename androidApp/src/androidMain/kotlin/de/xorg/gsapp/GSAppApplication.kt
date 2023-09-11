package de.xorg.gsapp

import android.app.Application
import de.xorg.gsapp.data.cache.AndroidCacheManager
import de.xorg.gsapp.data.cache.CacheManager
import de.xorg.gsapp.data.di.mainModule
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bind
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

    override val di: DI by DI.lazy {
        bind<CacheManager>() with singleton { AndroidCacheManager(applicationContext) }
        import(mainModule)
        import(androidXModule(this@GSAppApplication))
    }
}