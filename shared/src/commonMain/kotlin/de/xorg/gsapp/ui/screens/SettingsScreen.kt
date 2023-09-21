package de.xorg.gsapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier,
    navController: Navigator
) {
    Scaffold(modifier = modifier,
        topBar = {
        MediumTopAppBar(
            title = {
                Text("Einstellungen"/*text = stringResource(MR.strings.tab_substitutions),
                    fontFamily = fontFamilyResource(MR.fonts.LondrinaSolid.black),
                    style = MaterialTheme.typography.headlineMedium*/
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.goBack() }) {
                    Icon(Icons.Rounded.ArrowBack, "")
                }
            },
        )
    }) {
        Column(modifier = Modifier.padding(it)) {
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
            Text("Helloooooooooooooooooooooooooooooooooooooooo")
        }
    }
}