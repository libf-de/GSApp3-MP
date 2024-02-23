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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.ui.components.ExamChip
import de.xorg.gsapp.ui.components.state.EmptyLocalComponent
import de.xorg.gsapp.ui.components.state.FailedComponent
import de.xorg.gsapp.ui.components.state.LoadingComponent
import de.xorg.gsapp.ui.state.ComponentState
import de.xorg.gsapp.ui.tools.DateUtil
import de.xorg.gsapp.ui.tools.SupportMediumTopAppBar
import de.xorg.gsapp.ui.tools.spinAnimation
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.ExamPlanViewModel
import gsapp.composeapp.generated.resources.OrelegaOne_Regular
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.examplan_empty
import gsapp.composeapp.generated.resources.examplan_explain_coursework
import gsapp.composeapp.generated.resources.settings_title
import gsapp.composeapp.generated.resources.tab_exams
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * The exam plan-tab composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalLayoutApi::class, ExperimentalResourceApi::class
)
@Composable
fun ExamsScreen(
    navController: Navigator,
    bottomPadding: Dp = 84.dp
) {
    val viewModel: ExamPlanViewModel = koinViewModel(vmClass = ExamPlanViewModel::class)
    //val viewModel: GSAppViewModel = koinInject()

    /*val examState by viewModel.examState.collectAsStateWithLifecycle(
        ComponentState.EmptyLocal
    )*/
    val examState by viewModel.componentState.collectAsStateWithLifecycle()

    val course by viewModel.courseState.collectAsStateWithLifecycle(
        ExamCourse.default
    )

    var isFirst = false

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val windowSizeClass = calculateWindowSizeClass()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = bottomPadding),
        topBar = {
            SupportMediumTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(Res.string.tab_exams),
                            fontFamily = FontFamily(Font(Res.font.OrelegaOne_Regular)),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Row {
                            Text(
                                text = stringResource(Res.string.examplan_explain_coursework),
                                fontFamily = FontFamily(Font(Res.font.OrelegaOne_Regular)),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }

                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.spinAnimation(
                                examState.isLoading()
                            ))
                    }

                    IconButton(onClick = { navController.navigate(GSAppRoutes.SETTINGS) }) {
                        Icon(imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(Res.string.settings_title))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleCourse() },
            ) {
                Crossfade(targetState = course) {
                    ExamCourseIcon(it)
                }
            }
        }
    ) { innerPadding ->
        when(val exams = examState) {
            is ComponentState.StateWithData -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .windowSizeMargins(windowSizeClass)
                ) {
                    exams.data
                        .groupBy { it.date }
                        .forEach {
                            item {
                                ExamDateRow(
                                    date = it.key,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = if (!isFirst) 18.dp else 0.dp)
                                )
                            }
                            item {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    it.value.forEach {
                                        ExamChip(
                                            it,
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
                            isFirst = false
                        }

                    item {
                        // Bottom "overscroll" so no items are covered up by FAB
                        Spacer(Modifier.height(72.dp))
                    }
                }
            }

            is ComponentState.Loading -> {
                LoadingComponent(modifier = Modifier.fillMaxSize())
            }

            is ComponentState.EmptyLocal -> {
                EmptyLocalComponent(
                    where = Res.string.tab_exams
                )
            }

            is ComponentState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(Res.string.examplan_empty)
                    )
                }
            }

            is ComponentState.Failed -> {
                FailedComponent(
                    exception = exams.error,
                    where = Res.string.tab_exams,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ExamCourseIcon(course: ExamCourse) {
    Icon(
        imageVector = vectorResource(course.iconResource),
        contentDescription = stringResource(course.descriptiveResource),
        modifier = Modifier.size(24.dp)
    )
}

//                top = if (!isFirst) 12.dp else 0.dp
@Composable
private fun ExamDateRow(date: LocalDate, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(
            text = "${DateUtil.getWeekdayLong(date)}, " +
                    DateUtil.getDateAsString(date),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(text = date.todayUntilString())
    }

}

private fun LocalDate.todayUntilString(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysDiff = today.daysUntil(this)
    val weeksDiff = daysDiff / 7

    return if (daysDiff == 0)
        "heute"
    else if (daysDiff == 1)
        "morgen"
    else if (daysDiff > 7 && weeksDiff == 1)
        "in 1 Woche"
    else if (daysDiff > 7)
        "in $weeksDiff Wochen"
    else
        "in $daysDiff Tagen"
}