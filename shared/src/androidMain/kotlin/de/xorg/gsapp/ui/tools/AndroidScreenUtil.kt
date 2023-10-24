package de.xorg.gsapp.ui.tools

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

actual class ScreenUtil {
    actual companion object {
        actual fun getScreenDimensionsInPx(): IntSize {
            with(Resources.getSystem().displayMetrics) {
                return IntSize(this.widthPixels, this.heightPixels)
            }
        }

        actual fun getScreenOrientation(): ScreenOrientation {
            return getScreenDimensionsInPx().getOrientation()
        }
    }
}