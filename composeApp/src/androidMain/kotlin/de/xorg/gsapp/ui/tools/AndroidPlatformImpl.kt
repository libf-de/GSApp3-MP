package de.xorg.gsapp.ui.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPlatformImpl : PlatformInterface(), KoinComponent {
    @Composable
    override fun SendErrorReportButton(ex: Throwable) {
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                intent.data = Uri.parse("mailto:xorgmc+gsapp3@gmail.com")
                intent.putExtra(Intent.EXTRA_SUBJECT, "GSApp3-MP Fehlerreport")
                intent.putExtra(
                    Intent.EXTRA_TEXT, "(Beschreibe hier, was zu dem Fehler geführt hat)\n\n" +
                            "-- Fehlerdetails --\n" +
                            "Platform = Android\n" +
                            "Version = ???\n" +
                            "Stacktrace:\n" +
                            ex.stackTraceToString()
                )
                context.startActivity(intent)
            }
        ) {
            Text("Send mail")
        }
    }

    override fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        val context: Context by inject()
        context.startActivity(intent)
    }
}