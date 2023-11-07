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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.ExamCard
import de.xorg.gsapp.ui.components.state.EmptyLocalComponent
import de.xorg.gsapp.ui.components.state.FailedComponent
import de.xorg.gsapp.ui.components.state.LoadingComponent
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.state.isLoading
import de.xorg.gsapp.ui.tools.DateUtil
import de.xorg.gsapp.ui.tools.spinAnimation
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.GSAppViewModel
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.Navigator
import org.koin.compose.koinInject

/**
 * The exam plan-tab composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ExamsScreen(
    navController: Navigator
) {
    //val viewModel: GSAppViewModel = koinViewModel(vmClass = GSAppViewModel::class)
    val viewModel: GSAppViewModel = koinInject()

    val exams by viewModel.examFlow.collectAsStateWithLifecycle(
        Result.success(emptyList())
    )

    var isFirst = false

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val windowSizeClass = calculateWindowSizeClass()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = 84.dp),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = stringResource(MR.strings.tab_exams),
                        fontFamily = fontFamilyResource(MR.fonts.OrelegaOne.regular),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { viewModel.updateExams() }) {
                        Icon(imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.spinAnimation(
                                viewModel.uiState.examState.isLoading()
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
                    (exams.getOrNull() ?: emptyList())
                        .groupBy { it.date }
                        .forEach {
                            item {
                                Text(
                                    text = "${DateUtil.getWeekdayLong(it.key)}, " +
                                            DateUtil.getDateAsString(it.key),
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
                                ExamCard(exam = substitution)
                            }
                            isFirst = false
                        }
                }
            }

            UiState.LOADING -> {
                LoadingComponent(modifier = Modifier.fillMaxSize())
            }

            UiState.EMPTY_LOCAL -> {
                EmptyLocalComponent(
                    where = MR.strings.tab_exams
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
                        text = stringResource(MR.strings.examplan_empty)
                    )
                }
            }

            UiState.FAILED -> {
                FailedComponent(
                    exception = viewModel.uiState.examError,
                    where = MR.strings.tab_exams,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }
        }
    }
}