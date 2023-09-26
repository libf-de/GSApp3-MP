package de.xorg.gsapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformAlertDialog() {
    AlertDialog(onDismissRequest = { /*TODO*/ }, properties = DialogProperties()) {

    }
}