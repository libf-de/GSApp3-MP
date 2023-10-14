/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.SettingsItem
import de.xorg.gsapp.ui.components.settings.SettingsRadioDialog
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import getPlatformName
import kotlinx.collections.immutable.toImmutableList
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

/**
 * The app settings composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    val di = localDI()
    val viewModel: SettingsViewModel by di.instance()
    val pushUtil: PushNotificationUtil by di.instance()

    var showPushDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier,
        topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(MR.strings.settings_title))
            },
            navigationIcon = {
                IconButton(onClick = { navController.goBack() }) {
                    Icon(Icons.Rounded.ArrowBack, "")
                }
            },
        )
    }) {
        if(showPushDialog) {
            SettingsRadioDialog(
                icon = { Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "")},
                title = stringResource(MR.strings.push_dialog_title),
                message = stringResource(MR.strings.push_dialog_description),
                dismissText = stringResource(MR.strings.dialog_cancel),
                confirmText = stringResource(MR.strings.dialog_save),
                items = PushState.entries.toImmutableList(),
                selectedValue = viewModel.pushPreference.value,
                onDismiss = { showPushDialog = false },
                onConfirm = { selectedStringRes ->
                    showPushDialog = false
                    viewModel.setPush(selectedStringRes as PushState)
                }
            )
        }



        LazyColumn(modifier = modifier.padding(it).fillMaxSize()) {
            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(painter = painterResource(MR.images.filter_value),
                        contentDescription = "",
                        modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_filter),
                    subtitle = stringResource(viewModel.rolePreference.value.descriptiveResource,
                                              viewModel.filterPreference.value),
                    onClick = { navController.navigate(GSAppRoutes.SETTINGS_FILTER) }
                )
            }


            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(imageVector = Icons.Rounded.Notifications,
                                               contentDescription = "",
                                               modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_push),
                    subtitle = if(pushUtil.isSupported) stringResource(viewModel.pushPreference.value.labelResource)
                               else stringResource(MR.strings.push_unavailable, getPlatformName()),
                    onClick = { if(pushUtil.isSupported) showPushDialog = true }
                )

            }

        }
    }
}