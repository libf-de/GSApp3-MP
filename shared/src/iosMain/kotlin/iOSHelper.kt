import de.xorg.gsapp.data.di.appModule
import de.xorg.gsapp.ui.tools.PlatformInterface
import de.xorg.gsapp.ui.tools.IOSPlatformImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosModule = module {
    single<PlatformInterface> { IOSPlatformImpl() }
}

fun initKoin() {
    startKoin {
        modules(appModule() + iosModule)
    }
}