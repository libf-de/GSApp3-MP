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

package de.xorg.gsapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import de.xorg.gsapp.ui.screens.ExamsScreen
import de.xorg.gsapp.ui.screens.FoodplanScreen
import de.xorg.gsapp.ui.screens.SubstitutionsScreen
import de.xorg.gsapp.ui.screens.screens
import de.xorg.gsapp.ui.screens.settings.FilterSettingsScreen
import de.xorg.gsapp.ui.screens.settings.SettingsScreen
import de.xorg.gsapp.ui.theme.GSAppTheme
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

/**
 * This is the app main-common-entry composable, that applies the theme and mainly consists
 * of a hideable bottom bar and a NavHost, which navigates to the different app screens.
 * See GSAppRoutes.kt for the route uris.
 */

@Composable
fun GSApp() {
    GSAppTheme {
        val navigator = rememberNavigator()
        var hideNavBar by rememberSaveable { mutableStateOf(false) }
        val hideNavBarState = remember { MutableTransitionState(!hideNavBar) }

        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visibleState = hideNavBarState,
                    exit = fadeOut(),
                    enter = fadeIn(),
                ) {
                    NavigationBar {
                        val navBackStackEntry by navigator.currentEntry.collectAsState(null)
                        screens.forEach { screen ->
                            val title = stringResource(screen.title)
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painter = painterResource(screen.icon),
                                        contentDescription = title
                                    )
                                },
                                label = { Text(title) },
                                selected = (navBackStackEntry?.route?.route ?: "") == screen.route,
                                onClick = {
                                    if( (navBackStackEntry?.route?.route ?: "") != screen.route )
                                        navigator.navigate(
                                            screen.route, NavOptions(
                                                popUpTo = PopUpTo.First(true)
                                            )
                                        )
                                    hideNavBar = !screen.showNavbar
                                    hideNavBarState.targetState = screen.showNavbar
                                }
                            )
                        }
                    }
                }
            },
        ) {
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = GSAppRoutes.SUBSTITUTIONS, //TODO: Revert back to SUBSTITUTIONS
            ) {
                scene(
                    route = GSAppRoutes.SUBSTITUTIONS,
                    navTransition = NavTransition(
                        createTransition = fadeIn(),
                        resumeTransition = fadeIn(),
                        destroyTransition = fadeOut(),
                        pauseTransition = fadeOut(),
                    ),
                ) {
                    hideNavBar = false
                    hideNavBarState.targetState = true
                    SubstitutionsScreen(navigator)
                }

                scene(
                    route = GSAppRoutes.FOODPLAN,
                    navTransition = NavTransition(
                        createTransition = fadeIn(),
                        resumeTransition = fadeIn(),
                        destroyTransition = fadeOut(),
                        pauseTransition = fadeOut(),
                    ),
                ) {
                    hideNavBar = false
                    hideNavBarState.targetState = true
                    FoodplanScreen(navigator)
                }

                scene(
                    route = GSAppRoutes.EXAMS,
                    navTransition = NavTransition(
                        createTransition = fadeIn(),
                        resumeTransition = fadeIn(),
                        destroyTransition = fadeOut(),
                        pauseTransition = fadeOut(),
                    ),
                ) {
                    hideNavBar = false
                    hideNavBarState.targetState = true
                    ExamsScreen(navigator)
                }

                scene(
                    route = GSAppRoutes.SETTINGS,
                    navTransition = NavTransition(),
                ) {
                    hideNavBar = true
                    hideNavBarState.targetState = false
                    SettingsScreen(navigator)
                }

                scene(
                    route = GSAppRoutes.SETTINGS_FILTER,
                    navTransition = NavTransition(),
                ) {
                    hideNavBar = true
                    hideNavBarState.targetState = false
                    FilterSettingsScreen(navigator)
                }
            }
        }
    }
}
