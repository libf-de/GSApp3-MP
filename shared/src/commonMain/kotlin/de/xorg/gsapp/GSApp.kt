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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import de.xorg.gsapp.ui.tabs.FoodplanTab
import de.xorg.gsapp.ui.tabs.SubstitutionsTab
import de.xorg.gsapp.ui.theme.GSAppTheme


@Composable
fun GSApp() {
    GSAppTheme {
        val substitutionsTab = SubstitutionsTab()
        TabNavigator(substitutionsTab) {
            Scaffold(content = { CurrentTab() }, bottomBar = {
                NavigationBar {
                    //This should be SubstitutionTab and FoodplanTab without (), but needs to be
                    //initialized because they are internal classes instead of objects:
                    //(https://github.com/JetBrains/compose-multiplatform/issues/3444)
                    TabNavigationItem(substitutionsTab)
                    TabNavigationItem(FoodplanTab())
                }
            })
        }
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
