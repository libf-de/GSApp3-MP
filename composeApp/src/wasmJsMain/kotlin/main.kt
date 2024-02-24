import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.appModule
import de.xorg.gsapp.data.platform.WebPlatformImpl
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.push.WebPushUtilStub
import de.xorg.gsapp.data.sources.remote.GsWebsiteParser
import de.xorg.gsapp.data.sources.remote.MockParser
import de.xorg.gsapp.ui.tools.PlatformInterface
import org.koin.core.context.startKoin
import org.koin.dsl.module

/*import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow*/

val webModule = module {
    single<PlatformInterface> { WebPlatformImpl() }

    single<GsWebsiteParser> {
        MockParser()
    }

    single<PushNotificationUtil> {
        WebPushUtilStub()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(appModule() + webModule)
    }
    CanvasBasedWindow(canvasElementId = "ComposeTarget") { GSApp() }
}