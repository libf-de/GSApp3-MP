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

package de.xorg.gsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.fontFamilyResource


val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun getTypography(): Typography {
    return MaterialTheme.typography.copy(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        displayMedium = MaterialTheme.typography.displayMedium.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        displaySmall = MaterialTheme.typography.displaySmall.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        titleSmall = MaterialTheme.typography.titleSmall.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        bodySmall = MaterialTheme.typography.bodySmall.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        labelMedium = MaterialTheme.typography.labelMedium.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        labelSmall = MaterialTheme.typography.labelSmall.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        headlineMedium = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = fontFamilyResource(MR.fonts.Saira.variable),
        ),
    )
}

@Composable
expect fun GSAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)