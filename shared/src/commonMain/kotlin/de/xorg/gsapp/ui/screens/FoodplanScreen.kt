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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.GSAppViewModel
import de.xorg.gsapp.ui.components.FancyIndicator
import de.xorg.gsapp.ui.components.FoodplanCard
import de.xorg.gsapp.ui.components.LoadingComponent
import de.xorg.gsapp.ui.state.UiState
import de.xorg.gsapp.ui.tools.DateUtil
import de.xorg.gsapp.ui.tools.SettingsSource
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.stringResource
import getPlatformName
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import moe.tlaster.precompose.navigation.Navigator
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FoodplanScreen(
    navController: Navigator
) {
    val di = localDI()

    val viewModel by di.instance<GSAppViewModel>()
    val setSrc: SettingsSource by di.instance()

    val foodplan = viewModel.foodStateFlow.collectAsState().value

    val fpDates = foodplan.keys.toList()
    val fpFoods = foodplan.values.toList()

    /******************************************************/
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val listState = rememberLazyListState()

    //TODO Extract to own component
    var currentPageIndex by remember {
        mutableStateOf(0)
    }

    // TODO: Merge this
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val todayIndex = if(fpDates.contains(today)) fpDates.indexOf(today) else 0
    val pages = (fpDates.indices).toList()
    val pagerState = rememberPagerState(initialPage = todayIndex) { fpDates.size }

    /*with(pagerState) {
        LaunchedEffect(key1 = currentPageIndex) {
            launch {
                animateScrollToPage(
                    page = (currentPageIndex)
                )
            }
        }
    }*/

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = 84.dp),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(text = stringResource(MR.strings.tab_foodplan),
                        fontFamily = fontFamilyResource(MR.fonts.LondrinaSolid.black),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = if(getPlatformName() == "Desktop")
                    TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = if (listState.firstVisibleItemIndex == 0)
                            MaterialTheme.colorScheme.background
                        else
                            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                else TopAppBarDefaults.mediumTopAppBarColors(),
                actions = {
                    IconButton(onClick = { navController.navigate(GSAppRoutes.SETTINGS) },
                        modifier = Modifier.onGloballyPositioned { coords ->
                            setSrc.transformOrigin = TransformOrigin(pivotFractionX = coords.positionInRoot().x, pivotFractionY = coords.positionInRoot().y)
                        }) {
                        Icon(imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings") //TODO: Localize!
                    }
                }
            )
        }
    ) { innerPadding ->

        when (viewModel.uiState.foodplanState) {
            UiState.NORMAL -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    /*flingBehavior = flingBehavior*/
                ) {
                    // Tabs
                    stickyHeader {
                        Column {
                            ScrollableTabRow(
                                edgePadding = 20.dp,
                                containerColor = if (listState.firstVisibleItemIndex == 0)
                                    MaterialTheme.colorScheme.background
                                else
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                selectedTabIndex = pagerState.currentPage,
                                indicator = { tabPos ->
                                    FancyIndicator(modifier = Modifier
                                        .tabIndicatorOffset(tabPos[pagerState.currentPage])
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                pages.forEach { dayOffset ->
                                    val date = fpDates[dayOffset]
                                    Tab(
                                        modifier = Modifier.wrapContentWidth(),
                                        text = {
                                            Text("${DateUtil.getWeekdayLong(date)}\n" +
                                                    DateUtil.getDateAsString(date)
                                            )
                                        },
                                        selected = pagerState.currentPage == dayOffset,
                                        onClick = {
                                            currentPageIndex = dayOffset
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
                                    .padding(horizontal = 20.dp)
                                    .verticalScroll(
                                        rememberScrollState(),
                                        /*enabled = isScrolledDownState.currentState*/
                                    ),
                                verticalArrangement = Arrangement.Top
                            ) {
                                var foodNum = 0
                                fpFoods[page].forEach {
                                    val color = Color.hsl(
                                        (240 / fpFoods[page].size) * foodNum.toFloat(),
                                        0.6f, 0.5f
                                    )
                                    println("on page $foodNum -> ${(240 / fpFoods[page].size) * foodNum.toFloat()}")

                                    FoodplanCard(it, color, Modifier.padding(bottom = 8.dp))
                                    foodNum++
                                }



                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }

            UiState.EMPTY -> {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("(kein Speiseplan)")
                }
            }

            UiState.LOADING -> {
                LoadingComponent(modifier = Modifier.fillMaxSize())
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Fehler: ${viewModel.uiState.foodplanError.message}")
                }
            }
        }
    }


    /*BackHandler(enabled = onBackClicked != null) {
        if (onBackClicked != null) {
            onBackClicked()
        }
    }*/

    /*ClockBroadcastReceiver(systemAction = Intent.ACTION_TIME_TICK) {
        mensaViewModel.updateOpeningHourTexts(Category.ANY)
    }*/


}