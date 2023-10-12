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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
/*import com.moriatsushi.insetsx.ExperimentalSoftwareKeyboardApi
import com.moriatsushi.insetsx.imePadding*/
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.ClassListItem
import de.xorg.gsapp.ui.components.settings.SkeletonClassListItem
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.tools.LETTERS
import de.xorg.gsapp.ui.tools.classList
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

/**
 * This composable is the "substitution plan filter" settings dialog.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FilterSettingsScreen(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    val di = localDI()
    val viewModel: SettingsViewModel by di.instance()
    val teachers by viewModel.teachers.collectAsState(emptyList())

    // FilterValue to store in settings
    // (Teacher shortName / Student class)
    var filterVal by remember { mutableStateOf(viewModel.filterPreference.value) }
    var isValid by remember { mutableStateOf(filterVal.length > 2) }

    // Selected user role (Any/Student/Teacher)
    var roleVal by remember { mutableStateOf(viewModel.rolePreference.value) }

    /** Specific for Teacher **/
    //To focus both TextFields (long/short) at the same time
    val inputInteractionSource = remember { MutableInteractionSource() }
    //Long teacher name display value (right)
    var teacherLong by remember { mutableStateOf("") }
    var teacherCandidate: Teacher? by remember { mutableStateOf(teachers.firstOrNull {
        it.shortName == filterVal
    }) }

    val confirmFocusReq = remember { FocusRequester() }



    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(MR.strings.filter_dialog_title))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.goBack() }) {
                        Icon(Icons.Rounded.ArrowBack, "")
                    }
                },
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(start = 18.dp,
                         end = 18.dp)
                /*.imePadding()*/
        ) {
            Text(text = stringResource(MR.strings.filter_dialog_description),
                 textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterRole.entries.forEach {
                    Row(modifier = Modifier.selectable(
                        selected = roleVal == it,
                        onClick = {
                            if(roleVal != it) { //clear filter
                                filterVal = ""
                                teacherLong = ""
                                teacherCandidate = null
                            }
                            if(FilterRole.shouldStore(it)) //Store if requested
                                viewModel.setRoleAndFilter(it, filterVal)

                            roleVal = it //Set new role

                        }
                    )) {
                        RadioButton(selected = (roleVal == it),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(modifier = Modifier.padding(start = 4.dp).height(
                            IntrinsicSize.Max),
                            text = stringResource(it.labelResource) )
                    }
                }
            }


            AnimatedVisibility(roleVal == FilterRole.TEACHER) {
                Box(modifier = Modifier.padding(bottom = 8.dp)) {
                    OutlinedTextField(
                        value = teacherLong,
                        onValueChange = { },
                        readOnly = false,
                        label = { },
                        isError = !isValid,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                        interactionSource = inputInteractionSource,
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.setRoleAndFilter(FilterRole.ALL, "")
                                teacherLong = ""
                                filterVal = ""
                                teacherCandidate = null
                                isValid = false
                            }) {
                                Icon(Icons.Default.Clear, "")
                            }
                        },
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth().drawWithContent {
                            if (layoutDirection == LayoutDirection.Rtl) {
                                clipRect(right = size.width / 3f) {
                                    this@drawWithContent.drawContent()
                                }
                            } else {
                                clipRect(left = size.width / 3f) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                        }
                    )

                    OutlinedTextField(
                        value = filterVal,
                        onValueChange = {
                            filterVal = it.uppercase()  //Make input all-caps and alphanumeric
                                          .replace(Regex("[^A-Za-z0-9]"), "")
                            isValid = filterVal.length > 2 //Valid if TeacherShort +3 characters
                            teacherCandidate = teachers.firstOrNull { teacher ->
                                return@firstOrNull teacher.shortName == filterVal
                            }
                            teacherLong = teacherCandidate?.longName ?: ""
                            if(isValid) viewModel.setRoleAndFilter(roleVal, filterVal)
                        },
                        label = { Text(text = stringResource(MR.strings.filter_dialog_teacher_short)) },
                        interactionSource = inputInteractionSource,
                        isError = !isValid,
                        maxLines = 1,
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.setRoleAndFilter(FilterRole.ALL, "")
                                teacherLong = ""
                                filterVal = ""
                                teacherCandidate = null
                                isValid = false
                            }) {
                                Icon(Icons.Default.Clear, "")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .onKeyEvent {
                                println(it.key)
                                if (it.key == Key.Enter) {
                                    if (teacherCandidate != null)
                                        confirmFocusReq.requestFocus()
                                    return@onKeyEvent true
                                }
                                if (!LETTERS.contains(it.key)) return@onKeyEvent true
                                return@onKeyEvent false
                            }
                            .drawWithContent {
                                if (layoutDirection == LayoutDirection.Rtl) {
                                    clipRect(left = size.width * 2 / 3f) {
                                        this@drawWithContent.drawContent()
                                    }
                                } else {
                                    clipRect(right = size.width * 2 / 3f) {
                                        this@drawWithContent.drawContent()
                                    }
                                }
                            }
                    )

                }
            }


            LazyColumn(modifier = Modifier) {
                if(roleVal == FilterRole.TEACHER)
                    when (viewModel.teacherState) {
                        UiState.EMPTY -> item { Text("Es wurden keine Lehrer gefunden, bitte geben Sie ihr Kürzel von Hand ein.") }
                        UiState.FAILED -> item { Text("Lehrer konnten nicht geladen werden, bitte geben Sie ihr Kürzel von Hand ein.") }
                        else -> {}
                    }

                item {
                    Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
                }

                if(roleVal == FilterRole.TEACHER) {
                    if(viewModel.teacherState == UiState.NORMAL &&
                        teacherCandidate == null) {
                        items(teachers.filter { teacher ->
                            return@filter teacher.shortName
                                .lowercase()
                                .contains(filterVal.lowercase())
                                    || teacher.longName.contains(filterVal.lowercase())
                        }) {
                            Column(
                                Modifier
                                    .animateItemPlacement()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            filterVal = it.shortName.uppercase()
                                            teacherLong = it.longName
                                            isValid = true
                                            viewModel.setRoleAndFilter(FilterRole.TEACHER,
                                                                       it.shortName.uppercase())
                                        },
                                        indication = rememberRipple(bounded = true)
                                    )
                            ) {
                                Text(
                                    text = "${it.shortName} ⸺ ${it.longName}",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (it.longName == teacherLong)
                                        MaterialTheme.colorScheme.primary
                                    else Color.Unspecified
                                )
                                Divider(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(1.dp)
                                )
                            }
                        }
                    } else if(viewModel.teacherState == UiState.LOADING) {
                        for(i in 0..20) {
                            item {
                                SkeletonClassListItem(Modifier.animateItemPlacement())
                            }
                        }
                    }
                } else if(roleVal == FilterRole.STUDENT) {
                    items(classList) { className ->
                        ClassListItem(
                            label = className,
                            highlight = className == filterVal,
                            modifier = Modifier.animateItemPlacement())
                        {
                            filterVal = className
                            viewModel.setRoleAndFilter(FilterRole.STUDENT, className)
                        }
                    }
                }
            }

        }
    }
}