package de.xorg.gsapp

import MainView
import android.os.Bundle
import moe.tlaster.precompose.lifecycle.setContent
import androidx.core.view.WindowCompat
import moe.tlaster.precompose.lifecycle.PreComposeActivity

class MainActivity : PreComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MainView(applicationContext)
        }
    }
}