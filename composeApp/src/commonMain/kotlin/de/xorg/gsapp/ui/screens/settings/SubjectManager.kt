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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.ui.components.dialogs.InputTextDialog
import de.xorg.gsapp.ui.components.dialogs.SelectColorDialog
import de.xorg.gsapp.ui.components.settings.SubjectListItem
import de.xorg.gsapp.ui.components.state.FailedComponent
import de.xorg.gsapp.ui.components.state.LoadingComponent
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.back
import gsapp.composeapp.generated.resources.dialog_cancel
import gsapp.composeapp.generated.resources.dialog_delete_confirm
import gsapp.composeapp.generated.resources.dialog_delete_text
import gsapp.composeapp.generated.resources.dialog_delete_title
import gsapp.composeapp.generated.resources.reset
import gsapp.composeapp.generated.resources.settings_title
import gsapp.composeapp.generated.resources.subject_manager_add_desc
import gsapp.composeapp.generated.resources.subject_manager_add_title
import gsapp.composeapp.generated.resources.subject_manager_colorpicker_nullsubject
import gsapp.composeapp.generated.resources.subject_manager_colorpicker_title
import gsapp.composeapp.generated.resources.subject_manager_empty_text
import gsapp.composeapp.generated.resources.subject_manager_empty_title
import gsapp.composeapp.generated.resources.subject_manager_reset_dialog_adddefault
import gsapp.composeapp.generated.resources.subject_manager_reset_dialog_replace
import gsapp.composeapp.generated.resources.subject_manager_reset_dialog_text
import gsapp.composeapp.generated.resources.subject_manager_reset_dialog_title
import gsapp.composeapp.generated.resources.subject_manager_subjects
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject

/**
 * The app settings composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalResourceApi::class
)
@Composable
fun SubjectManager(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    //val viewModel: SettingsViewModel = koinViewModel(vmClass = SettingsViewModel::class)
    val viewModel: SettingsViewModel = koinInject()
    val windowSizeClass = calculateWindowSizeClass()

    val subjects by viewModel.subjects.collectAsStateWithLifecycle(
        initial = Result.success(emptyList()))

    var colorEditShow by remember { mutableStateOf(false) }
    var colorEditSubject by remember { mutableStateOf<Subject?>(null) }
    fun handleEditColor(subject: Subject) {
        colorEditSubject = subject
        colorEditShow = true
    }

    var showAddNewDialog by remember { mutableStateOf(false) }

    val showDeleteDialog = remember { mutableStateOf(false) }
    var subjectToDelete by remember { mutableStateOf(Subject.emptySubject) }
    fun handleDeleteSubject(subject: Subject) {
        subjectToDelete = subject
        showDeleteDialog.value = true
    }

    val showResetDialog = remember { mutableStateOf(false) }

    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.goBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                             contentDescription = stringResource(Res.string.back))
                    }
                },
                actions = {
                    if(viewModel.subjectsState == UiState.NORMAL_LOADING ||
                        viewModel.subjectsState == UiState.LOADING) {
                        IconButton(onClick = {}) {
                            CircularProgressIndicator()
                        }
                    }

                    IconButton(onClick = {
                            showResetDialog.value = true
                    }) {
                        Icon(imageVector = vectorResource(Res.drawable.reset),
                             contentDescription = stringResource(Res.string.subject_manager_reset_dialog_title))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddNewDialog = true },
            ) {
                Icon(imageVector = Icons.Rounded.Add,
                     contentDescription = stringResource(Res.string.subject_manager_add_title))
            }
        }
    ) {

        SelectColorDialog(
            visible = colorEditShow,
            onConfirm = { selectedColor ->
                colorEditShow = false
                if(colorEditSubject != null)
                    viewModel.updateSubject(colorEditSubject!!, color = selectedColor)
            },
            onCancel = {
                colorEditShow = false
            },
            preselectedColor = colorEditSubject?.color,
            title = stringResource(Res.string.subject_manager_colorpicker_title,
                colorEditSubject?.longName ?: stringResource(Res.string.subject_manager_colorpicker_nullsubject)
            ),
            pickMode = viewModel.colorpickerMode.value,
            onPickModeChanged = { pickerMode -> viewModel.setColorpickerMode(pickerMode) }
        )

        InputTextDialog(
            visible = showAddNewDialog,
            onConfirm = { shortName ->
                viewModel.addSubject(Subject(shortName = shortName))
                showAddNewDialog = false
            },
            onCancel = { showAddNewDialog = false },
            title = stringResource(Res.string.subject_manager_add_title),
            message = stringResource(Res.string.subject_manager_add_desc),

            modifier = Modifier.width(300.dp)
        )


        DeleteSubjectDialog(
            visibleState = showDeleteDialog,
            onDeletePressed = { viewModel.deleteSubject(subjectToDelete); subjectToDelete = Subject.emptySubject },
            onCancelPressed = { subjectToDelete = Subject.emptySubject },
            subjectToDelete = subjectToDelete
        )

        ResetSubjectsDialog(
            visibleState = showResetDialog,
            onResetPressed = { viewModel.resetSubjects() },
            onUpdatePressed = { viewModel.updateSubjects(true) }
        )

        when(viewModel.subjectsState) {
            UiState.LOADING -> {
                LoadingComponent(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }

            UiState.FAILED -> {
                FailedComponent(
                    exception = viewModel.subjectsError.value,
                    where = Res.string.subject_manager_subjects,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass),
                )
            }

            UiState.EMPTY, UiState.EMPTY_LOCAL -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.subject_manager_empty_title),
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = stringResource(Res.string.subject_manager_empty_text)
                    )
                }
            }

            UiState.NORMAL, UiState.NORMAL_LOADING, UiState.NORMAL_FAILED -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                        .windowSizeMargins(windowSizeClass),
                ) {
                    if(viewModel.subjectsState == UiState.NORMAL_FAILED) {
                        item {
                            Column {
                                FailedComponent(
                                    exception = viewModel.subjectsError.value,
                                    where = Res.string.subject_manager_subjects,
                                )
                                Text(viewModel.subjectsError.value.message.toString())
                                HorizontalDivider(Modifier.height(1.dp).fillMaxWidth())
                            }
                        }
                    }

                    items(
                        (subjects.getOrNull() ?: emptyList()).filter {
                                sub -> !sub.shortName.startsWith("&")
                        }.sortedBy { sub -> sub.shortName }
                    ){ subject ->
                        SubjectListItem(
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(500)
                            ),
                            subject = subject,
                            onDelete = ::handleDeleteSubject,
                            onNameEdited = { sub, name ->
                                viewModel.updateSubject(
                                    oldSubject = sub,
                                    longName = name
                                )
                            },
                            onColorClick = ::handleEditColor
                        )
                    }

                    item {
                        // Bottom "overscroll" so no items are covered up by FAB
                        Spacer(modifier.height(72.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun DeleteSubjectDialog(
    visibleState: MutableState<Boolean>,
    onDeletePressed: () -> Unit,
    onCancelPressed: () -> Unit,
    subjectToDelete: Subject,
    modifier: Modifier = Modifier
    ) {
    if(visibleState.value && subjectToDelete.isNotBlank()) {
        AlertDialog(
            onDismissRequest = {
                visibleState.value = false
                onCancelPressed()
            },
            title = {
                Text(
                    text = stringResource(Res.string.dialog_delete_title)
                )
            },
            text = {
                Text(
                    text = stringResource(
                        Res.string.dialog_delete_text,
                        subjectToDelete.shortName,
                        subjectToDelete.longName)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        visibleState.value = false
                        onDeletePressed()
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.dialog_delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        visibleState.value = false
                        onCancelPressed()
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.dialog_cancel)
                    )
                }
            },
            modifier = modifier
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun ResetSubjectsDialog(
    visibleState: MutableState<Boolean>,
    onResetPressed: () -> Unit,
    onUpdatePressed: () -> Unit,
) {
    if (visibleState.value) {
        BasicAlertDialog(onDismissRequest = {
            visibleState.value = false
        }) {
            Surface(
                color = AlertDialogDefaults.containerColor,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(all = 24.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.subject_manager_reset_dialog_title),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = stringResource(Res.string.subject_manager_reset_dialog_text),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = { onResetPressed(); visibleState.value = false },
                        shape = RoundedCornerShape(
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp,
                            topStart = 12.dp,
                            topEnd = 12.dp
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.subject_manager_reset_dialog_replace),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Button(
                        onClick = { onUpdatePressed(); visibleState.value = false },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.subject_manager_reset_dialog_adddefault),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Button(
                        onClick = { visibleState.value = false },
                        shape = RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.dialog_cancel),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

        }
    }
}