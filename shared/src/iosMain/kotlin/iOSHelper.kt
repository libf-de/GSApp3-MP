import de.xorg.gsapp.data.di.appModule
import de.xorg.gsapp.data.platform.PlatformInterface
import de.xorg.gsapp.data.platform.iOSPlatformImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosModule = module {
    single<PlatformInterface> { iOSPlatformImpl() }
}

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}