import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sources.settings.SettingsSource
import moe.tlaster.precompose.PreComposeApplication
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import platform.darwin.NSObject

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = PreComposeApplication {
    withDI({
        bind<SettingsSource>() with singleton { SettingsSource(NSObject()) }
        import(mainModule)
    }) { GSApp() }
}
