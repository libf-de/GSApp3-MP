import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.icerock.moko.resources.compose.stringResource
import de.xorg.gsapp.res.MR
import moe.tlaster.precompose.PreComposeWindow

fun main() = application {
    PreComposeWindow(onCloseRequest = ::exitApplication, title = stringResource(MR.strings.app_name)) {
        MainView()
    }
}