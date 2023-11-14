package de.xorg.gsapp.ui.tools

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.cinterop.zeroValue
import org.koin.core.component.KoinComponent
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UINavigationBar
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.statusBarManager

class IOSPlatformImpl : PlatformInterface(), KoinComponent {
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

    override fun openUrl(url: String) {
        val nsUrl = NSURL(string = url)
        nsUrl?.let {
            UIApplication.sharedApplication.openURL(it)
        }
    }
}