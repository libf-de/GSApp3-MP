package de.xorg.gsapp.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import de.xorg.gsapp.ui.GSAppViewModel
import dev.icerock.moko.resources.compose.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.components.FancyIndicator
import de.xorg.gsapp.ui.components.Foodplan
import de.xorg.gsapp.ui.components.FoodplanCard
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

//This should be an object, but to fix a "no implementation for FUN MISSING_DECLARATION" internal
//class is used (https://github.com/JetBrains/compose-multiplatform/issues/3444)
@OptIn(ExperimentalMaterial3Api::class)
internal class FoodplanTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(MR.strings.tab_foodplan)
            val icon = painterResource(MR.images.foodplan)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )

            }
        }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val di = localDI()

        val viewModel by di.instance<GSAppViewModel>()

        val foodplan = viewModel.foodStateFlow.collectAsState().value

        /******************************************************/
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val listState = rememberLazyListState()

        val snappingLayout = remember(listState) {
            SnapLayoutInfoProvider(
                listState
            )
        }

        val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

        //val configuration = LocalConfiguration.current

        //val screenHeight = configuration.screenHeightDp.dp

        val isScrolledDownState = remember {
            MutableTransitionState(false)
        }

        //TODO Extract to own component
        var currentPageIndex by remember {
            mutableStateOf(0)
        }

        // TODO: Merge this
        val pages = (0..7).toList()
        val pagerState = rememberPagerState(initialPage = 0) { pages.size }
        //val menuMap = mutableMapOf<Int, SnapshotStateList<Meal>>()
        //pages.forEach { offset ->
        //    menuMap[offset] = remember {
        //        mutableStateListOf()
        //    }
        //    menuUiStates.add(remember {
        //        mutableStateOf(UiState.LOADING)
        //    })
        //}


        with(pagerState) {
            LaunchedEffect(key1 = currentPageIndex) {
                launch {
                    animateScrollToPage(
                        page = (currentPageIndex)
                    )
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

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Red,
                        scrolledContainerColor = Color.Yellow,
                        navigationIconContentColor = Color.Green,
                        titleContentColor = Color.Cyan,
                        actionIconContentColor = Color.Magenta),
                    title = {
                        Text(
                            text = "Helo",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                            //style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = flingBehavior
            ) {
                item {
                    /*DetailHeader(
                        modifier = Modifier.padding(top = 8.dp),
                        foodProvider = foodProvider,
                        descriptionState = descriptionState,
                        additionalInfoState = additionalInfoState
                    )*/
                }
                // Tabs
                stickyHeader {
                    Column(
                        //  modifier = Modifier.padding(top = 16.dp)
                    ) {
                        // TODO: Check if pager support m3 tabrow
                        ScrollableTabRow(
                            edgePadding = 20.dp,
                            containerColor = if (listState.firstVisibleItemIndex == 0)
                                MaterialTheme.colorScheme.background
                            else
                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    3.dp
                                ),
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            selectedTabIndex = pagerState.currentPage,
                            indicator = { tabPositions ->
                                FancyIndicator(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                )

                            }
                        ) {
                            // Add tabs for all of our pages
                            pages.forEach { dayOffset ->
                                val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
                                    .plus(dayOffset.toLong(), DateTimeUnit.DAY)
                                Tab(
                                    modifier = Modifier.wrapContentWidth(),
                                    text = {
                                        val dayOfWeek = date.dayOfWeek.name
                                        val dateFormatted =
                                            "${date.dayOfMonth}.${date.monthNumber}.${date.year}"
                                        Text("$dayOfWeek\n$dateFormatted")
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
                    //.height(screenHeight - 64.dp), // 64dp is the height of the top appbar
                    //count = pages.size,
                    HorizontalPager(
                        modifier = Modifier
                            .fillParentMaxHeight(),
                        state = pagerState,
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        LaunchedEffect(key1 = "$page" + "menus") {
                            /*scope.*/launch {

                            //var menuUiState by mutableStateOf(UiState.LOADING)
                            //menuUiStates[page].value = UiState.LOADING

                            /*val menu = mensaViewModel.getMenu(
                                foodProvider.id!!,
                                page
                            )*/

                            /*val meals = if (menu.isSuccess) {
                                val temp = menu.getOrThrow().meals
                                menuUiStates[page].value = if (temp.isEmpty()) {
                                    UiState.NO_INFO
                                } else {
                                    UiState.NORMAL
                                }
                                temp
                            } else {
                                UiState.ERROR
                                emptyList()
                            }

                            menuMap[page]!!.clear()
                            menuMap[page]!!.addAll(meals)*/

                        }
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .verticalScroll(
                                    rememberScrollState(),
                                    enabled = isScrolledDownState.currentState
                                ),
                            verticalArrangement = Arrangement.Top
                            // contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            LaunchedEffect(listState) {
                                snapshotFlow { listState.firstVisibleItemIndex }
                                    .collect { isScrolledDownState.targetState = it != 0 }
                            }

                            AnimatedVisibility(
                                modifier = Modifier.clip(RoundedCornerShape(corner = CornerSize(28.dp))), // Looks better
                                visibleState = isScrolledDownState
                            ) {
                                Spacer(modifier = Modifier.height(48.dp))
                            }

                            foodplan[page].foods.forEach {
                                val color = Color.hsl((360/foodplan[page].foods.size)*page.toFloat(),
                                    1.0f, 1.0f)

                                FoodplanCard(it, color)
                            }

                            /*when (menuUiStates[page].value) {
                                UiState.NORMAL -> {
                                    menuMap[page]?.forEach { meal ->
                                        MealCard(
                                            modifier = Modifier.padding(bottom = 8.dp),
                                            meal = meal,
                                            role = mensaViewModel.role
                                        )
                                    }
                                }
                                UiState.NO_INFO -> {
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        LottieWithInfo(
                                            lottie = R.raw.no_info,
                                            iterations = 1,
                                            description = stringResource(
                                                id = R.string.text_lottie_no_meals
                                            )
                                        )
                                    }
                                }
                                UiState.LOADING -> {
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        LottieWithInfo(
                                            lottie = R.raw.loading_menus,
                                            description = stringResource(
                                                id = R.string.text_lottie_fetching_meals
                                            )
                                        )
                                    }
                                }
                                else -> {
                                    Column(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        LottieWithInfo(
                                            lottie = R.raw.error,
                                            description = stringResource(
                                                id = R.string.text_lottie_error
                                            )
                                        )
                                    }
                                }
                            }*/
                            Spacer(modifier = Modifier.height(80.dp))
                        }

                    }
                }

            }
        }
    }
}