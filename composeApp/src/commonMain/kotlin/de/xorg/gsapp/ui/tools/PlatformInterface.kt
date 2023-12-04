package de.xorg.gsapp.ui.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

abstract class PlatformInterface {
    @Composable
    abstract fun SendErrorReportButton(ex: Throwable)

    @Composable
    fun FeedbackButton(
        modifier: Modifier = Modifier
    ) {
        IconButton(onClick = { openUrl("https://agdsn.me/~xorg/gsapp3/feedback/") }) {
            Icon(painter = painterResource(MR.images.feedback),
                contentDescription = "Feedback"
            )
        }
    }

    abstract fun openUrl(url: String)
}