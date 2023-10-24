package de.xorg.gsapp.ui.tools

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.IntSize

expect class ScreenUtil() {
    companion object {
        fun getScreenDimensionsInPx(): IntSize
        fun getScreenOrientation(): ScreenOrientation
    }
}

fun IntSize.getOrientation(): ScreenOrientation {
    return if(this.width > this.height)
        ScreenOrientation.LANDSCAPE
    else if(this.width < this.height)
        ScreenOrientation.PORTRAIT
    else
        ScreenOrientation.SQUARE
}

enum class ScreenOrientation {
    LANDSCAPE,
    PORTRAIT,
    SQUARE
}