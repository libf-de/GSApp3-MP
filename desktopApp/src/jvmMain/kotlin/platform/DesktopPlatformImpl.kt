package platform

import androidx.compose.runtime.Composable
import de.xorg.gsapp.data.platform.PlatformInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DesktopPlatformImpl : PlatformInterface, KoinComponent {
    @Composable
    override fun sendErrorReport(ex: Throwable) {
        //TODO: Implement desktop mail sending!
    }
}