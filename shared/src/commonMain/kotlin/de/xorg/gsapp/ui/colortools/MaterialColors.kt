/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.xorg.gsapp.ui.colortools

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.color.utilities.ColorUtils
import de.xorg.gsapp.ui.colortools.annotations.ColorInt
import de.xorg.gsapp.ui.colortools.annotations.IntRange
import de.xorg.gsapp.ui.colortools.utilities.Blend
import de.xorg.gsapp.ui.colortools.utilities.Hct

/**
 * A utility class for common color variants used in Material themes.
 */
object MaterialColors {
    const val ALPHA_FULL = 1.00f
    const val ALPHA_MEDIUM = 0.54f
    const val ALPHA_DISABLED = 0.38f
    const val ALPHA_LOW = 0.32f
    const val ALPHA_DISABLED_LOW = 0.12f

    // TODO(b/199495444): token integration for color roles luminance values.
    // Tone means degrees of lightness, in the range of 0 (inclusive) to 100 (inclusive).
    // Spec: https://m3.material.io/styles/color/the-color-system/color-roles
    private const val TONE_ACCENT_LIGHT = 40
    private const val TONE_ON_ACCENT_LIGHT = 100
    private const val TONE_ACCENT_CONTAINER_LIGHT = 90
    private const val TONE_ON_ACCENT_CONTAINER_LIGHT = 10
    private const val TONE_SURFACE_CONTAINER_LIGHT = 94
    private const val TONE_SURFACE_CONTAINER_HIGH_LIGHT = 92
    private const val TONE_ACCENT_DARK = 80
    private const val TONE_ON_ACCENT_DARK = 20
    private const val TONE_ACCENT_CONTAINER_DARK = 30
    private const val TONE_ON_ACCENT_CONTAINER_DARK = 90
    private const val TONE_SURFACE_CONTAINER_DARK = 12
    private const val TONE_SURFACE_CONTAINER_HIGH_DARK = 17
    private const val CHROMA_NEUTRAL = 6


    /**
     * Calculates a color that represents the layering of the `overlayColor` on top of the
     * `backgroundColor`.
     */
    @ColorInt
    fun layer(backgroundColor: Color, overlayColor: Color): Color {
        return ColorUtils.compositeColors(overlayColor, backgroundColor)
    }

    /**
     * Calculates a new color by multiplying an additional alpha int value to the alpha channel of a
     * color in integer type.
     *
     * @param originalARGB The original color.
     * @param alpha The additional alpha [0-255].
     * @return The blended color.
     */
    fun compositeColorWithAlpha(
        originalColor: Color, @IntRange(from = 0, to = 255) alpha: Int
    ): Color {
        return Color(red = originalColor.red, green = originalColor.green, blue = originalColor.blue,
            alpha = originalColor.alpha * alpha / 255)
    }


    @ColorInt
    fun compositeARGBWithAlpha(
        @ColorInt originalARGB: Int, @IntRange(from = 0, to = 255) alpha: Int
    ): Int {
        return compositeColorWithAlpha(Color(originalARGB), alpha).toArgb()
    }

    /** Determines if a color should be considered light or dark.  */
    fun isColorLight(color: Color): Boolean {
        return color != Color.Transparent && ColorUtils.calculateLuminance(color) > 0.5
    }

    /**
     * A convenience function to harmonize any two colors provided, returns the color int of the
     * harmonized color, or the original design color value if color harmonization is not available.
     *
     * @param colorToHarmonize The color to harmonize.
     * @param colorToHarmonizeWith The primary color selected for harmonization.
     */
    @ColorInt
    fun harmonize(@ColorInt colorToHarmonize: Int, @ColorInt colorToHarmonizeWith: Int): Int {
        return Blend.harmonize(colorToHarmonize, colorToHarmonizeWith)
    }

    /**
     * Returns the [ColorRoles] object generated from the provided input color.
     *
     * @param color The input color provided for generating its associated four color roles.
     * @param isLightTheme Whether the input is light themed or not, true if light theme is enabled.
     */
    fun getColorRoles(@ColorInt color: Int, isLightTheme: Boolean): ColorRoles {
        return if (isLightTheme) ColorRoles(
            getColorRole(color, TONE_ACCENT_LIGHT),
            getColorRole(color, TONE_ON_ACCENT_LIGHT),
            getColorRole(color, TONE_ACCENT_CONTAINER_LIGHT),
            getColorRole(color, TONE_ON_ACCENT_CONTAINER_LIGHT)
        ) else ColorRoles(
            getColorRole(color, TONE_ACCENT_DARK),
            getColorRole(color, TONE_ON_ACCENT_DARK),
            getColorRole(color, TONE_ACCENT_CONTAINER_DARK),
            getColorRole(color, TONE_ON_ACCENT_CONTAINER_DARK)
        )
    }

    @ColorInt
    private fun getColorRole(@ColorInt color: Int, @IntRange(from = 0, to = 100) tone: Int): Int {
        val hctColor: Hct = Hct.fromInt(color)
        hctColor.setTone(tone.toDouble())
        return hctColor.toInt()
    }

    @ColorInt
    private fun getColorRole(
        @ColorInt color: Int, @IntRange(from = 0, to = 100) tone: Int, chroma: Int
    ): Int {
        val hctColor: Hct = Hct.fromInt(getColorRole(color, tone))
        hctColor.setChroma(chroma.toDouble())
        return hctColor.toInt()
    }
}