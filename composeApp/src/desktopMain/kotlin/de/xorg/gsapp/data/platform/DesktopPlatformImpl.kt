package platform

import androidx.compose.runtime.Composable
import de.xorg.gsapp.ui.tools.PlatformInterface
import org.koin.core.component.KoinComponent
import java.awt.Desktop

class DesktopPlatformImpl : PlatformInterface(), KoinComponent {
    @Composable
    override fun SendErrorReportButton(ex: Throwable) {
        //TODO: Implement desktop mail sending!
    }

    override fun openUrl(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
            Desktop.getDesktop().browse(java.net.URI(url))
    }
}