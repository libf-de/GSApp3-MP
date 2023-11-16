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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.FancyIndicator
import de.xorg.gsapp.ui.components.FoodplanCard
import de.xorg.gsapp.ui.components.state.EmptyLocalComponent
import de.xorg.gsapp.ui.components.state.FailedComponent
import de.xorg.gsapp.ui.components.state.LoadingComponent
import de.xorg.gsapp.ui.state.ComponentState
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.state.isLoading
import de.xorg.gsapp.ui.tools.DateUtil
import de.xorg.gsapp.ui.tools.SupportMediumTopAppBar
import de.xorg.gsapp.ui.tools.SupportTopAppBarDefaults
import de.xorg.gsapp.ui.tools.spinAnimation
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.FoodplanViewModel
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

/**
 * The foodplan-tab composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun FoodplanScreen(
    navController: Navigator
) {
    val viewModel: FoodplanViewModel = koinViewModel(vmClass = FoodplanViewModel::class)
    //val viewModel: GSAppViewModel = koinInject()

    val windowSizeClass = calculateWindowSizeClass()

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val foodplanState by viewModel.foodplanState.collectAsStateWithLifecycle()

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val todayIndex = if(foodplanState.dataOrDefault(emptyMap()).keys.contains(today))
                        foodplanState.dataOrDefault(emptyMap()).keys.indexOf(today)
                     else 0
    val pagerState = rememberPagerState(initialPage = todayIndex) {
        foodplanState.dataOrDefault(emptyMap()).size
    }
    var currentPageIndex by remember { mutableStateOf(todayIndex) }

    var appBarColor by remember { mutableStateOf(Color.Transparent) }

    with(pagerState) {
        LaunchedEffect(currentPageIndex) {
            launch {
                animateScrollToPage(
                    page = currentPageIndex
                )
            }
        }
    }


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = 84.dp),
        topBar = {
            SupportMediumTopAppBar(
                title = {
                    Text(text = stringResource(MR.strings.tab_foodplan),
                        fontFamily = fontFamilyResource(MR.fonts.OrelegaOne.regular),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = /*if(getPlatformName() != "Android") //TopAppBar currently does not color correctly on desktop/ios TODO: Remove when fixed
                            SupportTopAppBarDefaults.supportMediumTopAppBarColors(
                                containerColor = if (listState.firstVisibleItemIndex == 0)
                                    MaterialTheme.colorScheme.background
                                else
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                         else */SupportTopAppBarDefaults.supportMediumTopAppBarColors(),
                actions = {
                    /** Settings button **/
                    IconButton(
                        onClick = { navController.navigate(GSAppRoutes.SETTINGS) },
                        modifier = Modifier
                    ) {
                        IconButton(onClick = { viewModel.updateFoodplan() }) {
                            Icon(imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                                modifier = Modifier.spinAnimation(
                                    viewModel.uiState.foodplanState.isLoading()
                                ))
                        }

                        IconButton(onClick = { navController.navigate(GSAppRoutes.SETTINGS) }) {
                            Icon(imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(MR.strings.settings_title))
                        }
                    }
                },
                onBackgroundColorChanged = { color -> appBarColor = color }
            )
        }
    ) { innerPadding ->
        when(val foodplan = foodplanState) {
            is ComponentState.StateWithData -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp) ) {
                    // Tabs
                    stickyHeader {
                        Column {
                            ScrollableTabRow(
                                edgePadding = 20.dp,
                                containerColor = appBarColor/*if (listState.firstVisibleItemIndex == 0)
                                    MaterialTheme.colorScheme.background
                                else
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),*/,
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                selectedTabIndex = pagerState.currentPage,
                                indicator = { tabPos ->
                                    FancyIndicator(modifier = if(tabPos.size > pagerState.currentPage)
                                        Modifier.tabIndicatorOffset(tabPos[pagerState.currentPage])
                                    else Modifier
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                foodplan.data.entries.forEachIndexed { index, entry ->
                                    Tab(
                                        modifier = Modifier.wrapContentWidth(),
                                        text = {
                                            Text("${DateUtil.getWeekdayLong(entry.key)}\n" +
                                                    DateUtil.getDateAsString(entry.key)
                                            )
                                        },
                                        selected = pagerState.currentPage == index,
                                        onClick = {
                                            currentPageIndex = index
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        HorizontalPager(
                            modifier = Modifier
                                .fillParentMaxHeight(),
                            state = pagerState,
                            verticalAlignment = Alignment.Top
                        ) { page ->
                            Column(
                                modifier = Modifier
                                    .windowSizeMargins(windowSizeClass)
                                    /*.padding(horizontal = 20.dp)*/
                                    .verticalScroll(
                                        rememberScrollState(),
                                        /*enabled = isScrolledDownState.currentState*/
                                    ),
                                verticalArrangement = Arrangement.Top
                            ) {
                                if(page >= foodplan.data.size) {
                                    Text("Fehler: aktuelle Seite ist nicht im Speiseplan enthalten :/")
                                    return@Column
                                }

                                var foodNum = 0
                                val todayFoods = foodplan.data.values.toList()[page]

                                todayFoods.forEach {
                                    val color = Color.hsl(
                                        (240 / todayFoods.size) * foodNum.toFloat(),
                                        0.6f, 0.5f
                                    )
                                    Napier.d { "on page $foodNum -> " +
                                            (240 / todayFoods.size) * foodNum.toFloat() }

                                    FoodplanCard(
                                        food = it,
                                        menuNumber = foodNum + 1,
                                        color = color,
                                        modifier = Modifier.padding(bottom = 8.dp))

                                    foodNum++
                                }

                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }

            is ComponentState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .windowSizeMargins(windowSizeClass),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(MR.strings.foodplan_empty)
                    )
                }
            }

            is ComponentState.EmptyLocal -> {
                EmptyLocalComponent(
                    where = MR.strings.tab_foodplan,
                    windowSizeClass = windowSizeClass
                )
            }

            is ComponentState.Loading -> {
                LoadingComponent(modifier = Modifier.fillMaxSize())
            }

            is ComponentState.Failed -> {
                FailedComponent(
                    exception = foodplan.error,
                    where = MR.strings.tab_foodplan,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }
        }
    }
}

private fun <T, E : Throwable> ComponentState<T, E>.dataOrDefault(default: T): T {
    return when(this) {
        is ComponentState.Normal -> data
        else -> default
    }
}
