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

package de.xorg.gsapp.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.NoteCard
import de.xorg.gsapp.ui.components.SubstitutionCard
import de.xorg.gsapp.ui.components.state.EmptyLocalComponent
import de.xorg.gsapp.ui.components.state.FailedComponent
import de.xorg.gsapp.ui.components.state.LoadingComponent
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.state.isLoading
import de.xorg.gsapp.ui.state.isNormal
import de.xorg.gsapp.ui.tools.DateUtil
import de.xorg.gsapp.ui.tools.DateUtil.Companion.getWeekdayLongRes
import de.xorg.gsapp.ui.tools.PlatformInterface
import de.xorg.gsapp.ui.tools.SupportMediumTopAppBar
import de.xorg.gsapp.ui.tools.platformSpecificScrollBehavior
import de.xorg.gsapp.ui.tools.spinAnimation
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.GSAppViewModel
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.Navigator
import org.koin.compose.koinInject



/**
 * This is the substitution plan-tab composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SubstitutionsScreen(
    navController: Navigator
) {
    //val viewModel: GSAppViewModel = koinViewModel(vmClass = GSAppViewModel::class)
    val viewModel: GSAppViewModel = koinInject()
    val windowSizeClass = calculateWindowSizeClass()

    /*val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )*/

    val sds by viewModel.subFlow.collectAsStateWithLifecycle(Result.success(SubstitutionSet.EMPTY))
    var isFirst = false

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(viewModel.uiState.substitutionState) {
        println("SubstitutionState: ${viewModel.uiState.substitutionState}")
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = 84.dp),
        topBar = {
            SupportMediumTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(MR.strings.tab_substitutions),
                            fontFamily = fontFamilyResource(MR.fonts.OrelegaOne.regular),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        if(viewModel.uiState.substitutionState.isNormal() && sds.getOrNull() != null) {
                            Text(
                                text = stringResource(
                                    MR.strings.subplan_date_header_for,
                                    sds.getOrDefault(SubstitutionSet.EMPTY).dateStr
                                ),
                                fontFamily = fontFamilyResource(MR.fonts.OrelegaOne.regular),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                },
                scrollBehavior = scrollBehavior,
                actions = {
                    koinInject<PlatformInterface>().FeedbackButton()

                    IconButton(onClick = { viewModel.updateSubstitutions() }) {
                        Icon(imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.spinAnimation(
                                viewModel.uiState.substitutionState.isLoading()
                            ))
                    }

                    IconButton(onClick = { navController.navigate(GSAppRoutes.SETTINGS) }) {
                        Icon(imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(MR.strings.settings_title))
                    }
                }
            )
        }
    ) { innerPadding ->
        when(viewModel.uiState.substitutionState) {
            UiState.NORMAL,
            UiState.NORMAL_LOADING,
            UiState.NORMAL_FAILED -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .windowSizeMargins(windowSizeClass)
                ) {
                    val notes = sds.getOrDefault(SubstitutionSet.EMPTY).notes
                    if(notes.isNotBlank()) {
                        item {
                            NoteCard(
                                text = notes,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                    sds.getOrDefault(SubstitutionSet.EMPTY)
                        .substitutions
                        .forEach {
                            item {
                                Text(
                                    text = it.key,
                                    modifier = Modifier.padding(
                                        start = 12.dp,
                                        top = if(!isFirst) 12.dp else 0.dp
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            items(it.value) { substitution ->
                                SubstitutionCard(value = substitution)
                            }
                            isFirst = false
                        }
                }
            }

            UiState.LOADING -> {
                LoadingComponent(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }

            UiState.EMPTY -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(MR.strings.subplan_empty)
                    )
                }
            }

            UiState.EMPTY_LOCAL -> {
                EmptyLocalComponent(
                    where = MR.strings.tab_substitutions,
                    windowSizeClass = windowSizeClass
                )
            }

            UiState.FAILED -> {
                FailedComponent(
                    exception = viewModel.uiState.substitutionError,
                    where = MR.strings.tab_substitutions,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }
        }
    }
}

@Composable
private fun getDateAsStringForHeader(date: LocalDate): String {
    val monthString = when(date.monthNumber) {
        1 -> stringResource(MR.strings.jan)
        2 -> stringResource(MR.strings.feb)
        3 -> stringResource(MR.strings.mar)
        4 -> stringResource(MR.strings.apr)
        5 -> stringResource(MR.strings.may)
        6 -> stringResource(MR.strings.jun)
        7 -> stringResource(MR.strings.jul)
        8 -> stringResource(MR.strings.aug)
        9 -> stringResource(MR.strings.sep)
        10 -> stringResource(MR.strings.oct)
        11 -> stringResource(MR.strings.nov)
        12 -> stringResource(MR.strings.dec)
        else -> ""
    }
    return stringResource(MR.strings.subplan_date_header_fmt)
        .replace("%w", stringResource(getWeekdayLongRes(date)) )
        .replace("%d", date.dayOfMonth.toString())
        .replace("%m", monthString)
        .replace("%y", date.year.toString())
}
