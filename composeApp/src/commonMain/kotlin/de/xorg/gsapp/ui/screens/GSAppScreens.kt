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

import de.xorg.gsapp.GSAppRoutes
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.exams
import gsapp.composeapp.generated.resources.foodplan
import gsapp.composeapp.generated.resources.substitutions
import gsapp.composeapp.generated.resources.tab_exams
import gsapp.composeapp.generated.resources.tab_foodplan
import gsapp.composeapp.generated.resources.tab_substitutions
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

/**
 * This class contains the composables that will be displayed as tabs in the main application.
 * The sealed class is used to provide the associated icons and labels, and whether to show
 * the tab control (should always be true here, otherwise the user can't navigate to other tabs)
 */
val screens = listOf(
    GSAppScreen.Substitutions,
    GSAppScreen.Foodplan,
    GSAppScreen.Exams
)

@OptIn(ExperimentalResourceApi::class)
sealed class GSAppScreen(
    val route: String,
    val title: StringResource,
    val icon: DrawableResource,
    val showNavbar: Boolean = true
) {
    data object Substitutions : GSAppScreen(
        route = GSAppRoutes.SUBSTITUTIONS,
        title = Res.string.tab_substitutions,
        icon = Res.drawable.substitutions
    )

    data object Foodplan : GSAppScreen(
        route = GSAppRoutes.FOODPLAN,
        title = Res.string.tab_foodplan,
        icon = Res.drawable.foodplan
    )

    data object Exams : GSAppScreen(
        route = GSAppRoutes.EXAMS,
        title = Res.string.tab_exams,
        icon = Res.drawable.exams
    )

}