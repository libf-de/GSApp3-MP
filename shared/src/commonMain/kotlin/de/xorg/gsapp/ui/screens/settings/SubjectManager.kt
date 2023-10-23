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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.InputTextDialog
import de.xorg.gsapp.ui.components.settings.SelectColorDialog
import de.xorg.gsapp.ui.components.settings.SubjectListItem
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.collect
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.lifecycle.LocalLifecycleOwner
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

/**
 * The app settings composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectManager(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    val di = localDI()
    val viewModel: SettingsViewModel by di.instance()

    /*val subFlow by remember { mutableStateOf(viewModel.subjects) }


    val subjects by sub*/

    val sub = viewModel.subjects.collectAsStateWithLifecycle(
        initial = Result.success(emptyList()))

    var subjects by remember { mutableStateOf(Result.success(listOf<Subject>())) }

    LaunchedEffect(Unit) {
        viewModel.subjects.collect {
            subjects = it
            println(subjects)
        }
    }

    var colorEditShow by remember { mutableStateOf(false) }
    var colorEditSubject by remember { mutableStateOf<Subject?>(null) }
    var showAddNewDialog by remember { mutableStateOf(false) }

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
            }
        )
    }) {
        if(colorEditShow) {
            SelectColorDialog(
                onConfirm = { selectedColor ->
                    colorEditShow = false
                    if(colorEditSubject != null)
                        viewModel.updateSubject(colorEditSubject!!, color = selectedColor)
                },
                onCancel = {
                    colorEditShow = false
                },
                preselectedColor = colorEditSubject?.color,
                title = stringResource(MR.strings.subject_manager_colorpicker_title,
                    colorEditSubject?.longName ?: stringResource(MR.strings.subject_manager_colorpicker_nullsubject)
                ),
                pickMode = viewModel.colorpickerMode.value,
                onPickModeChanged = { pickerMode -> viewModel.setColorpickerMode(pickerMode) }
            )
        }

        if(showAddNewDialog) {
            InputTextDialog(
                onConfirm = { shortName ->
                    viewModel.addSubject(Subject(shortName = shortName))
                    showAddNewDialog = false
                },
                onCancel = { showAddNewDialog = false },
                title = stringResource(MR.strings.subject_manager_add_title),
                message = stringResource(MR.strings.subject_manager_add_desc)
            )
        }

        // TODO: Handle subjectsState properly!
        LazyColumn(modifier = modifier.padding(it).fillMaxSize()) {
            if(subjects.isSuccess) {
                item {
                    Text(
                        subjects.getOrDefault(listOf(Subject("leer"))).firstOrNull()?.longName ?: "leer"
                    )
                }
            }

            items(subjects.getOrNull() ?: emptyList()) { subject ->
                SubjectListItem(
                    modifier = Modifier,
                    subject = subject,
                    onDelete = { sub -> viewModel.deleteSubject(sub) },
                    onNameEdited = { sub, name ->
                        viewModel.updateSubject(
                            oldSubject = sub,
                            longName = name
                        )
                    },
                    onColorClick = { sub ->
                        colorEditSubject = sub
                        colorEditShow = true
                    }
                )
            }
        }



        FloatingActionButton(
            onClick = { showAddNewDialog = true },
        ) {
            Icon(Icons.Rounded.Add, "Add new subject")
        }
    }
}