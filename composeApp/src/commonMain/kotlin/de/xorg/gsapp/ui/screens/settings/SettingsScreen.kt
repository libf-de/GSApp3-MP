/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.dialogs.SettingsRadioDialog
import de.xorg.gsapp.ui.components.settings.SettingsItem
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import getPlatformName
import kotlinx.collections.immutable.toImmutableList
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.Navigator
import org.koin.compose.koinInject

/**
 * The app settings composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SettingsScreen(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    //val viewModel: SettingsViewModel = koinViewModel(vmClass = SettingsViewModel::class)
    val viewModel: SettingsViewModel = koinInject()
    val pushUtil: PushNotificationUtil = koinInject()
    val windowSizeClass = calculateWindowSizeClass()

    val pushState by viewModel.pushFlow.collectAsStateWithLifecycle(PushState.default)
    val filterState by viewModel.filterFlow.collectAsStateWithLifecycle(Filter.NONE)

    var showPushDialog by remember { mutableStateOf(false) }
    val firebaseLoading by viewModel.firebaseLoading.collectAsStateWithLifecycle()

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
        if (firebaseLoading) {
            Dialog(
                onDismissRequest = {  },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment= Alignment.Center,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.0.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 36.dp, vertical = 18.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(text = stringResource(MR.strings.push_firebase_loading))
                    }

                }
            }
        }


        if(showPushDialog) {
            SettingsRadioDialog(
                icon = { Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "")},
                title = stringResource(MR.strings.push_dialog_title),
                message = stringResource(MR.strings.push_dialog_description),
                dismissText = stringResource(MR.strings.dialog_cancel),
                confirmText = stringResource(MR.strings.dialog_save),
                items = PushState.entries.toImmutableList(),
                selectedValue = pushState,
                onDismiss = { showPushDialog = false },
                onConfirm = { selectedStringRes ->
                    showPushDialog = false
                    viewModel.setPush(selectedStringRes as PushState)
                }
            )
        }


        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .windowSizeMargins(windowSizeClass)) {
            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(painter = painterResource(MR.images.filter_value),
                        contentDescription = "",
                        modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_filter),
                    subtitle = stringResource(filterState.role.descriptiveResource, filterState.value),
                    onClick = { navController.navigate(GSAppRoutes.SETTINGS_FILTER) }
                )
            }


            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(imageVector = Icons.Rounded.Notifications,
                                               contentDescription = "",
                                               modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_push),
                    subtitle = if(pushUtil.isSupported) stringResource(pushState.labelResource)
                               else stringResource(MR.strings.push_unavailable, getPlatformName()),
                    onClick = { if(pushUtil.isSupported) showPushDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(painter = painterResource(MR.images.subjects),
                        contentDescription = "",
                        modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_subjects),
                    subtitle = stringResource(MR.strings.pref_subjects_desc),
                    onClick = { navController.navigate(GSAppRoutes.SETTINGS_SUBJECTS) }
                )
            }

            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(Icons.Rounded.Info,
                        contentDescription = "",
                        modifier = mod, tint = tint) },
                    title = "GSApp3 Multiplat(t)form",
                    subtitle = "Version 1.0.5-dev",
                    onClick = { }
                )
            }

        }
    }
}