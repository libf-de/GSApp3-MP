import androidx.compose.ui.window.ComposeUIViewController
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import moe.tlaster.precompose.PreComposeApplication
import org.kodein.di.compose.withDI

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = PreComposeApplication {
    withDI({
        import(mainModule)
    }) { GSApp() }
}
