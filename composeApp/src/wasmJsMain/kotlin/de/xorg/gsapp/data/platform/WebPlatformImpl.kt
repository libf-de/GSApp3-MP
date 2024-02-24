package de.xorg.gsapp.data.platform

import androidx.compose.runtime.Composable
import de.xorg.gsapp.ui.tools.PlatformInterface
import org.koin.core.component.KoinComponent

class WebPlatformImpl : PlatformInterface(), KoinComponent {
    @Composable
    override fun SendErrorReportButton(ex: Throwable) {
        //TODO: Implement desktop mail sending!
    }

    override fun openUrl(url: String) {

    }
}