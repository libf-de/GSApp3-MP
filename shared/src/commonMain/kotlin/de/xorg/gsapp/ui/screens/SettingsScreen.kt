package de.xorg.gsapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.SettingsItem
import de.xorg.gsapp.ui.components.settings.SettingsRadioDialog
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {

    val di = localDI()
    val viewModel: SettingsViewModel by di.instance()
    val showRoleDialog = remember { mutableStateOf(false) }
    val showPushDialog = remember { mutableStateOf(false) }

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
        if(showRoleDialog.value) {
            SettingsRadioDialog(
                icon ={ Icon(painter = painterResource(MR.images.filter), contentDescription = "")},
                title = "Rolle wählen",
                message = "Wähle aus, ob der Vertretungsplan nach Klasse oder nach Lehrer gefiltert werden soll.",
                dismissText = "Abbrechen",
                confirmText = "Speichern",
                items = FilterRole.values().toList(),
                selectedValue = viewModel.rolePreference.value,
                onDismiss = { showRoleDialog.value = false },
                onConfirm = { selectedStringRes ->
                    showRoleDialog.value = false
                    viewModel.setRole(selectedStringRes as FilterRole)
                }
            )
        }

        if(showPushDialog.value) {
            SettingsRadioDialog(
                icon = { Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "")},
                title = "Pushbenachrichtigung",
                message = "Wähle aus ob du keine, nur wenn es dich betrifft oder immer über neue Vertretungspläne benachrichtigt werden willst.",
                dismissText = "Abbrechen",
                confirmText = "Speichern",
                items = PushState.values().toList(),
                selectedValue = viewModel.pushPreference.value,
                onDismiss = { showPushDialog.value = false },
                onConfirm = { selectedStringRes ->
                    showPushDialog.value = false
                    viewModel.setPush(selectedStringRes as PushState)
                }
            )
        }

        LazyColumn(modifier = modifier) {
            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(painter = painterResource(MR.images.filter),
                        contentDescription = "",
                        modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_filter),
                    subtitle = stringResource(viewModel.rolePreference.value.getValue()),
                    onClick = { showRoleDialog.value = true }
                )
            }

            if(viewModel.rolePreference.value != FilterRole.ALL) {
                item {
                    SettingsItem(
                        icon = { mod, tint -> Icon(painter = painterResource(MR.images.filter_value),
                            contentDescription = "",
                            modifier = mod, tint = tint) },
                        title = stringResource(
                            if(viewModel.rolePreference.value == FilterRole.STUDENT) MR.strings.pref_filter_val_student
                            else MR.strings.pref_filter_val_teacher),
                        subtitle = stringResource(viewModel.rolePreference.value.getValue()),
                        onClick = { }
                    )
                }
            }

            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(imageVector = Icons.Rounded.Notifications,
                                               contentDescription = "",
                                               modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_filter),
                    subtitle = stringResource(viewModel.rolePreference.value.getValue()),
                    onClick = { showPushDialog.value = true }
                )
            }

        }
    }
}