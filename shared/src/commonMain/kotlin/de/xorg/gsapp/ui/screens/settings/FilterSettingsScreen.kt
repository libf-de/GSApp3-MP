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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
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
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.settings.ClassListItem
import de.xorg.gsapp.ui.components.settings.SkeletonClassListItem
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.tools.LETTERS
import de.xorg.gsapp.ui.tools.classList
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.Navigator
import org.koin.compose.koinInject

/**
 * This composable is the "substitution plan filter" settings dialog.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun FilterSettingsScreen(
    navController: Navigator,
    modifier: Modifier = Modifier,
) {
    //val viewModel: SettingsViewModel = koinViewModel(vmClass = SettingsViewModel::class)
    val viewModel: SettingsViewModel = koinInject()
    val windowSizeClass = calculateWindowSizeClass()

    val teachers by viewModel.teachers.collectAsStateWithLifecycle(Result.success(emptyList()))
    val filterState by viewModel.filterFlow.collectAsStateWithLifecycle(Filter.NONE)

    // FilterValue to store in settings
    // (Teacher shortName / Student class)
    var filterVal by remember { mutableStateOf(filterState.value) }
    var isValid by remember { mutableStateOf(filterVal.length > 2) }

    // Selected user role (Any/Student/Teacher)
    var roleVal by remember { mutableStateOf(filterState.role) }

    LaunchedEffect(filterState) {
        filterVal = filterState.value
        roleVal = filterState.role
    }

    /** Specific for Teacher **/
    //To focus both TextFields (long/short) at the same time
    val inputInteractionSource = remember { MutableInteractionSource() }
    //Long teacher name display value (right)
    var teacherLong by remember { mutableStateOf("") }
    var teacherCandidate: Teacher? by remember {
        mutableStateOf(
            teachers
                .getOrDefault(emptyList())
                .firstOrNull {
                    it.shortName == filterVal
                }
        )
    }

    val confirmFocusReq = remember { FocusRequester() }

    fun setRole(role: Filter.Role) {
        if(roleVal != role) { //clear filter
            filterVal = ""
            teacherLong = ""
            teacherCandidate = null
        }
        if(Filter.Role.shouldStore(role)) //Store if requested
            viewModel.setFilter(Filter(role, filterVal))

        roleVal = role //Set new role
    }

    fun handleTeacherShortChange(value: String) {
        filterVal = value.uppercase()  //Make input all-caps and alphanumeric
            .replace(Regex("[^A-Za-z0-9]"), "")
        isValid = filterVal.length > 2 //Valid if TeacherShort +3 characters
        teacherCandidate = teachers
            .getOrDefault(emptyList())
            .firstOrNull { teacher ->
                return@firstOrNull teacher.shortName == filterVal
            }
        teacherLong = teacherCandidate?.longName ?: ""
        if(isValid) viewModel.setFilter(Filter(roleVal, filterVal))
    }

    fun clearFilter() {
        viewModel.setFilter(Filter.NONE)
        teacherLong = ""
        filterVal = ""
        teacherCandidate = null
        isValid = false
    }

    fun handleOnShortEnterPressed() {
        if (teacherCandidate != null) confirmFocusReq.requestFocus()
    }

    Scaffold(modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(MR.strings.filter_dialog_title))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.goBack() }) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                },
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .windowSizeMargins(windowSizeClass)
                /*.imePadding()*/
        ) {
            Text(text = stringResource(MR.strings.filter_dialog_description),
                 textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Filter.Role.entries.forEach {
                    Row(modifier = Modifier.selectable(
                        selected = roleVal == it,
                        onClick = {
                            setRole(it)
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


            TeacherTextField(
                longValue = teacherLong,
                isValid = isValid,
                interactionSource = inputInteractionSource,
                onLongClearPressed = {
                    clearFilter()
                },
                shortValue = filterVal,
                onShortValueChange = {
                    handleTeacherShortChange(it)
                },
                onShortEnterPressed = ::handleOnShortEnterPressed,
                onShortClearPressed = {
                    viewModel.setFilter(Filter.NONE)
                    teacherLong = ""
                    filterVal = ""
                    teacherCandidate = null
                    isValid = false
                },
                visible = roleVal == Filter.Role.TEACHER
            )


            LazyColumn(modifier = Modifier) {
                when {
                    roleVal == Filter.Role.TEACHER && viewModel.teacherState == UiState.EMPTY
                    -> item { Text("Es wurden keine Lehrer gefunden, bitte geben Sie ihr Kürzel von Hand ein.") }

                    roleVal == Filter.Role.TEACHER && viewModel.teacherState == UiState.FAILED
                    -> item { Text("Lehrer konnten nicht geladen werden, bitte geben Sie ihr Kürzel von Hand ein.") }
                }

                item {
                    Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
                }

                when {
                    roleVal == Filter.Role.TEACHER &&
                    viewModel.teacherState == UiState.NORMAL &&
                    teacherCandidate == null -> {
                        items(teachers.getOrDefault(emptyList()).filter { teacher ->
                            return@filter teacher.shortName.lowercase().contains(filterVal.lowercase())
                                    || teacher.longName.contains(filterVal.lowercase())
                        }) {
                            TextItem(
                                onClick = {
                                    filterVal = it.shortName.uppercase()
                                    teacherLong = it.longName
                                    isValid = true
                                    viewModel.setFilter(
                                        Filter(
                                            role = Filter.Role.TEACHER,
                                            value = it.shortName.uppercase()
                                        )
                                    )
                                },
                                text = "${it.shortName} ⸺ ${it.longName}",
                                selected = it.longName == teacherLong
                            )
                        }
                    }

                    roleVal == Filter.Role.TEACHER && viewModel.teacherState == UiState.LOADING -> {
                        items(List(20) { "" }) {
                            SkeletonClassListItem(Modifier.animateItemPlacement())
                        }
                    }

                    roleVal == Filter.Role.STUDENT -> {
                        items(classList) { className ->
                            ClassListItem(
                                label = className,
                                highlight = className == filterVal,
                                modifier = Modifier.animateItemPlacement())
                            {
                                filterVal = className
                                viewModel.setFilter(
                                    Filter(
                                        role = Filter.Role.STUDENT,
                                        value = className
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun TeacherTextField(
    longValue: String,
    isValid: Boolean,
    interactionSource: MutableInteractionSource,
    onLongClearPressed: () -> Unit = {},
    shortValue: String,
    onShortValueChange: (String) -> Unit,
    onShortClearPressed: () -> Unit = {},
    onShortEnterPressed: () -> Unit = {},
    visible: Boolean
) {
    AnimatedVisibility(visible) {
        Box(modifier = Modifier.padding(bottom = 8.dp)) {
            OutlinedTextField(
                value = longValue,
                onValueChange = { },
                readOnly = false,
                label = { },
                isError = !isValid,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                interactionSource = interactionSource,
                trailingIcon = {
                    IconButton(onClick = onLongClearPressed) {
                        Icon(Icons.Default.Clear, "")
                    }
                },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToRect(ratio = 1 / 3f, direction = ClipDirection.End)
            )

            OutlinedTextField(
                value = shortValue,
                onValueChange = onShortValueChange,
                label = { Text(text = stringResource(MR.strings.filter_dialog_teacher_short)) },
                interactionSource = interactionSource,
                isError = !isValid,
                maxLines = 1,
                trailingIcon = {
                    IconButton(onClick = onShortClearPressed) {
                        Icon(Icons.Default.Clear, "")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            onShortEnterPressed()
                            return@onKeyEvent true
                        }
                        if (!LETTERS.contains(it.key)) return@onKeyEvent true
                        return@onKeyEvent false
                    }
                    .clipToRect(ratio = 2 / 3f, direction = ClipDirection.Start)

            )

        }
    }
}

@Composable
private fun TextItem(
    onClick: () -> Unit,
    text: String,
    selected: Boolean
) {
    Column(
        Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
                indication = rememberRipple(bounded = true)
            )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else Color.Unspecified
        )
        Divider(
            modifier = Modifier.fillMaxWidth()
                .height(1.dp)
        )
    }
}

private enum class ClipDirection {
    Start, End
}

private fun Modifier.clipToRect(ratio: Float, direction: ClipDirection): Modifier {
    return this.drawWithContent {
        var clipRight = direction == ClipDirection.End
        if(layoutDirection == LayoutDirection.Ltr) {
            clipRight = !clipRight
        }
        if (clipRight) {
            clipRect(right = size.width * ratio) {
                this@drawWithContent.drawContent()
            }
        } else {
            clipRect(left = size.width * ratio) {
                this@drawWithContent.drawContent()
            }
        }
    }
}