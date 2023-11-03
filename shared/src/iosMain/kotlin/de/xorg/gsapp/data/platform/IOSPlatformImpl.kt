package de.xorg.gsapp.data.platform

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication

class IOSPlatformImpl : PlatformInterface, KoinComponent {
    @Composable
    override fun SendErrorReportButton(ex: Throwable) {
        Button(
            onClick = {
                try {
                    val components = NSURLComponents()
                    components.scheme = "mailto"
                    components.path = "xorgmc+gsapp3@gmail.com"
                    components.queryItems = listOf(
                        NSURLQueryItem(name = "subject", value = "GSApp3-MP Fehlerreport"),
                        NSURLQueryItem(name = "body", value = "(Beschreibe hier, was zu dem Fehler geführt hat)\n\n" +
                                "-- Fehlerdetails --\n" +
                                "Platform = iOS\n" +
                                "Version = ???\n" +
                                "Stacktrace:\n" +
                                ex.stackTraceToString())
                    )

                    val url = components.URL()

                    if(url != null) {
                        UIApplication.sharedApplication.openURL(url)
                    } else {
                        println("Failed to create mailto URL :/")
                    }
                } catch(ex: Exception) {
                    ex.printStackTrace()
                }
            }
        ) {
            Text("Send mail")
        }
    }
}