package de.xorg.gsapp.ui.screens

import de.xorg.gsapp.GSAppRoutes
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource


val screens = listOf(
    GSAppScreen.Substitutions,
    GSAppScreen.Foodplan
)

sealed class GSAppScreen(
    val route: String,
    val title: StringResource,
    val icon: ImageResource,
    val showNavbar: Boolean
) {
    data object Substitutions : GSAppScreen(
        route = GSAppRoutes.SUBSTITUTIONS,
        title = MR.strings.tab_substitutions,
        icon = MR.images.substitutions,
        showNavbar = true
    )

    data object Foodplan : GSAppScreen(
        route = GSAppRoutes.FOODPLAN,
        title = MR.strings.tab_foodplan,
        icon = MR.images.foodplan,
        showNavbar = true
    )

}