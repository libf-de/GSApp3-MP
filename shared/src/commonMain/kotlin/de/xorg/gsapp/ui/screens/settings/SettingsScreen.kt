package de.xorg.gsapp.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.ClassListItem
import de.xorg.gsapp.ui.components.SkeletonClassListItem
import de.xorg.gsapp.ui.components.settings.SettingsFilterDialog
import de.xorg.gsapp.ui.components.settings.SettingsItem
import de.xorg.gsapp.ui.components.settings.SettingsRadioDialog
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.tools.LETTERS
import de.xorg.gsapp.ui.tools.classList
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.toImmutableList
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    val di = localDI()
    val viewModel: SettingsViewModel by di.instance()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showPushDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier,
        topBar = {
        TopAppBar(
            title = {
                Text(text = stringResource(MR.strings.settings_title)/*,
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
        if(showFilterDialog) {
            SettingsFilterDialog(
                icon = { },
                title = stringResource(MR.strings.filter_dialog_title),
                message = stringResource(MR.strings.filter_dialog_description),
                teacherList = viewModel.teachers.value,
                teacherState = viewModel.teacherState,
                selectedValue = viewModel.filterPreference.value,
                confirmText = stringResource(MR.strings.dialog_save),
                dismissText = stringResource(MR.strings.dialog_cancel),
                onDismiss = {
                    showFilterDialog = false
                },
                onConfirm = { role, filter ->
                    showFilterDialog = false
                    viewModel.setRoleAndFilter(role, filter)
                }
            )
        }

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
                    subtitle = stringResource(viewModel.pushPreference.value.labelResource),
                    onClick = { showPushDialog = true }
                )

            }

        }
    }
}