import com.moriatsushi.insetsx.WindowInsetsUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import moe.tlaster.precompose.PreComposeApplication
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import platform.Foundation.NSUserDefaults.Companion.standardUserDefaults

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = WindowInsetsUIViewController { PreComposeApplication {
    withDI({
        bind<Settings>() with singleton { NSUserDefaultsSettings(standardUserDefaults()) }
        //bind<SettingsSource>() with singleton { SettingsSource(NSObject()) }
        import(mainModule)
    }) { GSApp() }
} }
