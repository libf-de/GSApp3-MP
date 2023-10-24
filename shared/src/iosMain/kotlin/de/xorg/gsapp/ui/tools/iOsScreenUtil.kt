package de.xorg.gsapp.ui.tools

import androidx.compose.ui.unit.IntSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication
import platform.UIKit.UIInterfaceOrientationPortrait
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow

actual class ScreenUtil {

    actual companion object {
        @OptIn(ExperimentalForeignApi::class)
        actual fun getScreenDimensionsInPx(): IntSize {
            val mainWindow = UIApplication.sharedApplication.windows.filter {
                if(it !is UIWindow) return@filter false
                return@filter it.isKeyWindow()
            }.firstOrNull()

            if(mainWindow !is UIWindow) return IntSize(0, 0)

            return mainWindow.frame.useContents {
                return@useContents IntSize(this.size.width.toInt(), this.size.height.toInt())
            }
        }

        actual fun getScreenOrientation(): ScreenOrientation {
            /*return when(UIApplication.sharedApplication.statusBarOrientation) {
                UIInterfaceOrientationPortrait -> ScreenOrientation.PORTRAIT
                else -> ScreenOrientation.LANDSCAPE
            }*/

            return getScreenDimensionsInPx().getOrientation()
        }
    }
}