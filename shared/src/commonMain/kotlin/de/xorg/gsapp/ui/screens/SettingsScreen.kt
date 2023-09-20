package de.xorg.gsapp.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: Navigator
) {
    TopAppBar(
        title = { Text("Einstellungen") },
        navigationIcon = {
            IconButton(onClick = { navController.goBack() }) {
                Icon(Icons.Rounded.ArrowBack, "")
            }
        },
    )
}