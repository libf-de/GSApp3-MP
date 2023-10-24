package de.xorg.gsapp.ui.tools

import androidx.compose.ui.unit.IntSize
import java.awt.Toolkit

actual class ScreenUtil {
    actual companion object {
        actual fun getScreenDimensionsInPx(): IntSize {
            return with(Toolkit.getDefaultToolkit().screenSize) {
                IntSize(this.width, this.height)
            }
        }

        actual fun getScreenOrientation(): ScreenOrientation {
            return getScreenDimensionsInPx().getOrientation()
        }
    }
}