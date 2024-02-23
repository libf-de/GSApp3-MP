package de.xorg.gsapp.ui.tools

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.feedback
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.vectorResource

abstract class PlatformInterface {
    @Composable
    abstract fun SendErrorReportButton(ex: Throwable)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun FeedbackButton(
        modifier: Modifier = Modifier
    ) {
        IconButton(onClick = { openUrl("https://agdsn.me/~xorg/gsapp3/feedback/") }) {
            Icon(imageVector = vectorResource(Res.drawable.feedback),
                contentDescription = "Feedback"
            )
        }
    }

    abstract fun openUrl(url: String)
}