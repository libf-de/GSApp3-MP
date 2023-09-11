package de.xorg.gsapp.ui.theme

import androidx.compose.runtime.Composable

@Composable
expect fun GSAppTheme(
    darkTheme: Boolean = kmpIsSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)

@Composable
expect fun kmpIsSystemInDarkTheme(): Boolean