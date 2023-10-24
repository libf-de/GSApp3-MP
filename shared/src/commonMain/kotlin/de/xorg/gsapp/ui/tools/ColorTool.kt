package de.xorg.gsapp.ui.tools

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun foregroundColorForBackground(backgroundColor: Color): Color {
    val luminance = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue)
    return if (luminance > 0.5) Color.Black else Color.White
}

@OptIn(ExperimentalStdlibApi::class)
fun Color?.toHexString(): String {
    if(this == null) return ""

    val red = (this.red * 255).toInt().toHexString(HexFormat.UpperCase).takeLast(2)
    val green = (this.green * 255).toInt().toHexString(HexFormat.UpperCase).takeLast(2)
    val blue = (this.blue * 255).toInt().toHexString(HexFormat.UpperCase).takeLast(2)
    return "#${red}${green}${blue}"
}

