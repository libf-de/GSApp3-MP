import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import java.util.prefs.Preferences

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() = withDI({
    bind<Settings>() with singleton { PreferencesSettings(
        Preferences.userRoot()
    ) }
    import(mainModule)
}) {
    GSApp()
}

@Preview
@Composable
fun AppPreview() {
    App()
}