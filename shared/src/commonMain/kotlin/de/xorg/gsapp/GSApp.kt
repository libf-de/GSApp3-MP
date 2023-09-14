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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import de.xorg.gsapp.ui.GSAppViewModel
import de.xorg.gsapp.ui.components.SubstitutionCard
import de.xorg.gsapp.ui.tabs.FoodplanTab
import de.xorg.gsapp.ui.tabs.SubstitutionsTab
import org.kodein.di.compose.localDI
import org.kodein.di.instance


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun GSApp() {
    val di = localDI()

    val viewModel by di.instance<GSAppViewModel>()

    val sds = viewModel.subStateFlow.collectAsState().value

    //val navController = rememberAnimatedNavController()

    var hideNavBar by rememberSaveable { mutableStateOf(false) }

    val hideNavBarState = remember { MutableTransitionState(!hideNavBar) }

    /*Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visibleState = hideNavBarState,
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }
                ),
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }
                )
            ) {
                NavigationBar() {}
            }

        },
        content = {
            LazyColumn(
                modifier = Modifier
            ) {
                items(sds.substitutions) { substitution ->
                    SubstitutionCard(value = substitution)
                }
            }
        }
    )*/

    TabNavigator(SubstitutionsTab()) {
        Scaffold(content = { CurrentTab() }, bottomBar = {
            NavigationBar {
                //This should be SubstitutionTab and FoodplanTab without (), but needs to be
                //initialized because they are internal classes instead of objects:
                //(https://github.com/JetBrains/compose-multiplatform/issues/3444)
                TabNavigationItem(SubstitutionsTab())
                TabNavigationItem(FoodplanTab())
            }
        })
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    //BottomNavigationItem(
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
        label = { Text(tab.options.title) }
    )

}
