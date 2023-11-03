package de.xorg.gsapp.data.platform

import androidx.compose.runtime.Composable

fun interface PlatformInterface {
    @Composable
    fun SendErrorReportButton(ex: Throwable)
}