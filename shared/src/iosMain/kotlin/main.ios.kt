import androidx.compose.ui.window.ComposeUIViewController
import de.xorg.gsapp.GSApp

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { GSApp() }