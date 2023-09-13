import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.icerock.moko.resources.compose.stringResource
import de.xorg.gsapp.res.MR

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = stringResource(MR.strings.app_name)) {
        MainView()
    }
}