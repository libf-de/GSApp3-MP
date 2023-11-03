package de.xorg.gsapp.data.platform

import androidx.compose.runtime.Composable

interface PlatformInterface {
    @Composable
    fun sendErrorReport(ex: Throwable)
}