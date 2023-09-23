package de.xorg.gsapp.ui.screens

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
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.SettingsFilterDialog
import de.xorg.gsapp.ui.components.settings.SettingsItem
import de.xorg.gsapp.ui.components.settings.SettingsRadioDialog
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
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

    var showRoleDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showPushDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier,
        topBar = {
        TopAppBar(
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
        if(showRoleDialog) {
            SettingsRadioDialog(
                icon ={ Icon(painter = painterResource(MR.images.filter), contentDescription = "")},
                title = "Rolle wählen",
                message = "Wähle aus, ob der Vertretungsplan nach Klasse oder nach Lehrer gefiltert werden soll.",
                dismissText = "Abbrechen",
                confirmText = "Speichern",
                items = FilterRole.values().toList(),
                selectedValue = viewModel.rolePreference.value,
                onDismiss = { showRoleDialog = false },
                onConfirm = { selectedStringRes ->
                    showRoleDialog = false
                    viewModel.setRole(selectedStringRes as FilterRole)
                }
            )
        }

        if(showFilterDialog) {
            SettingsFilterDialog(
                icon = { },
                title = "Ich bin",
                message = "Wählen Sie Ihren Namen aus um nur Stunden die Sie vertreten anzuzeigen.",
                teacherList = viewModel.teachers.value,
                teacherState = viewModel.teacherState.value,
                selectedValue = viewModel.filterPreference.value,
                confirmText = "Speichern",
                dismissText = "Abbrechen",
                onDismiss = {
                    showFilterDialog = false
                }
            )
        }

        if(showPushDialog) {
            SettingsRadioDialog(
                icon = { Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "")},
                title = "Pushbenachrichtigung",
                message = "Wähle aus ob du keine, nur wenn es dich betrifft oder immer über neue Vertretungspläne benachrichtigt werden willst.",
                dismissText = "Abbrechen",
                confirmText = "Speichern",
                items = PushState.values().toList(),
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
                    title = stringResource(
                        if(viewModel.rolePreference.value == FilterRole.STUDENT) MR.strings.pref_filter_val_student
                        else MR.strings.pref_filter_val_teacher),
                    subtitle = stringResource(viewModel.rolePreference.value.labelResource),
                    onClick = { showFilterDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = { mod, tint -> Icon(imageVector = Icons.Rounded.Notifications,
                                               contentDescription = "",
                                               modifier = mod, tint = tint) },
                    title = stringResource(MR.strings.pref_push),
                    subtitle = stringResource(viewModel.pushPreference.value.labelResource),
                    onClick = { showPushDialog = true }
                )

            }

        }
    }
}