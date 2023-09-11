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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.xorg.gsapp.ui.GSAppViewModel
import de.xorg.gsapp.ui.components.SubstitutionCard
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

    Scaffold(
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
    )
}