package de.xorg.gsapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun GSAppTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if(darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        /*typography = getTypography(),*/
        content = content
    )
}