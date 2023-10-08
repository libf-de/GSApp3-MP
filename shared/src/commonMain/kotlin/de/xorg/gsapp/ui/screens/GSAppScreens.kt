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

package de.xorg.gsapp.ui.screens

import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

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

sealed class GSAppScreen(
    val route: String,
    val title: StringResource,
    val icon: ImageResource,
    val showNavbar: Boolean = true
) {
    data object Substitutions : GSAppScreen(
        route = GSAppRoutes.SUBSTITUTIONS,
        title = MR.strings.tab_substitutions,
        icon = MR.images.substitutions
    )

    data object Foodplan : GSAppScreen(
        route = GSAppRoutes.FOODPLAN,
        title = MR.strings.tab_foodplan,
        icon = MR.images.foodplan
    )

    data object Exams : GSAppScreen(
        route = GSAppRoutes.EXAMS,
        title = MR.strings.tab_exams,
        icon = MR.images.exams
    )

}