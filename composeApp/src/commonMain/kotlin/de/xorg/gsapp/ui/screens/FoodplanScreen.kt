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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.ui.components.FancyIndicator
import de.xorg.gsapp.ui.components.FoodplanCard
import de.xorg.gsapp.ui.components.state.EmptyLocalComponent
import de.xorg.gsapp.ui.components.state.FailedComponent
import de.xorg.gsapp.ui.components.state.LoadingComponent
import de.xorg.gsapp.ui.state.ComponentState
import de.xorg.gsapp.ui.state.dataOrDefault
import de.xorg.gsapp.ui.tools.DateUtil
import de.xorg.gsapp.ui.tools.PlatformInterface
import de.xorg.gsapp.ui.tools.SupportMediumTopAppBar
import de.xorg.gsapp.ui.tools.SupportTopAppBarDefaults
import de.xorg.gsapp.ui.tools.spinAnimation
import de.xorg.gsapp.ui.tools.windowSizeMargins
import de.xorg.gsapp.ui.viewmodels.FoodplanViewModel
import gsapp.composeapp.generated.resources.OrelegaOne_Regular
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.foodorder
import gsapp.composeapp.generated.resources.foodplan_empty
import gsapp.composeapp.generated.resources.settings_title
import gsapp.composeapp.generated.resources.tab_foodorder
import gsapp.composeapp.generated.resources.tab_foodplan
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject

/**
 * The foodplan-tab composable
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalResourceApi::class
)
@Composable
fun FoodplanScreen(
    navController: Navigator,
    bottomPadding: Dp = 84.dp
) {
    val viewModel: FoodplanViewModel = koinViewModel(vmClass = FoodplanViewModel::class)
    val platformInterface: PlatformInterface = koinInject()

    // Used to set correct margins according to device size
    val windowSizeClass = calculateWindowSizeClass()

    // Used for collapsing TopAppBar
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    // Composable state
    val foodplanState by viewModel.componentState.collectAsStateWithLifecycle()

    // Used to preselect current day
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val todayIndex = with(foodplanState.dataOrDefault(emptyMap())) {
        if(this.keys.contains(today))
            this.keys.indexOf(today)
        else 0
    }
    val pagerState = rememberPagerState(initialPage = todayIndex) {
        foodplanState.dataOrDefault(emptyMap()).size
    }
    var currentPageIndex by remember { mutableStateOf(todayIndex) }

    // Matches the Days-Tabbar color to the TopAppBar color
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
            .padding(bottom = bottomPadding),
        topBar = {
            SupportMediumTopAppBar(
                title = {
                    Text(text = stringResource(Res.string.tab_foodplan),
                        fontFamily = FontFamily(Font(Res.font.OrelegaOne_Regular)),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = SupportTopAppBarDefaults.supportMediumTopAppBarColors(),
                actions = {
                    IconButton(onClick = {
                        platformInterface.openUrl("https://schulkueche-bestellung.de/")
                    }) {
                        Icon(imageVector = vectorResource(Res.drawable.foodorder),
                             contentDescription = stringResource(Res.string.tab_foodorder))
                    }

                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.spinAnimation(
                                foodplanState.isLoading()
                            ))
                    }

                    IconButton(onClick = { navController.navigate(GSAppRoutes.SETTINGS) }) {
                        Icon(imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(Res.string.settings_title))
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
                    verticalArrangement = Arrangement.spacedBy(16.dp) ) {
                    // Tabs
                    stickyHeader {
                        Column {
                            DayTabRow(
                                days = foodplan.data.keys,
                                selectedTabIndex = pagerState.currentPage,
                                onTabSelected = { currentPageIndex = it },
                                containerColor = appBarColor
                            )
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
                                    .verticalScroll(rememberScrollState()),
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
                        text = stringResource(Res.string.foodplan_empty)
                    )
                }
            }

            is ComponentState.EmptyLocal -> {
                EmptyLocalComponent(
                    where = Res.string.tab_foodplan,
                    windowSizeClass = windowSizeClass
                )
            }

            is ComponentState.Loading -> {
                LoadingComponent(modifier = Modifier.fillMaxSize())
            }

            is ComponentState.Failed -> {
                FailedComponent(
                    exception = foodplan.error,
                    where = Res.string.tab_foodplan,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowSizeMargins(windowSizeClass)
                )
            }
        }
    }
}

@Composable
private fun DayTabRow(
    days: Set<LocalDate>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    containerColor: Color
) {
    ScrollableTabRow(
        edgePadding = 20.dp,
        containerColor = containerColor,
        contentColor = MaterialTheme.colorScheme.onBackground,
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPos ->
            FancyIndicator(modifier = if(tabPos.size > selectedTabIndex)
                Modifier.tabIndicatorOffset(tabPos[selectedTabIndex])
            else Modifier
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        days.forEachIndexed { index, date ->
            Tab(
                modifier = Modifier.wrapContentWidth(),
                text = {
                    Text("${DateUtil.getWeekdayLong(date)}\n" +
                            DateUtil.getDateAsString(date)
                    )
                },
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}
