import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import org.kodein.di.compose.withDI

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() = withDI({
    import(mainModule)
}) {
    GSApp()
}

@Preview
@Composable
fun AppPreview() {
    App()
}