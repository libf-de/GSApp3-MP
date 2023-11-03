package platform

import androidx.compose.runtime.Composable
import de.xorg.gsapp.data.platform.PlatformInterface
import org.koin.core.component.KoinComponent

class DesktopPlatformImpl : PlatformInterface, KoinComponent {
    @Composable
    override fun SendErrorReportButton(ex: Throwable) {
        //TODO: Implement desktop mail sending!
    }
}