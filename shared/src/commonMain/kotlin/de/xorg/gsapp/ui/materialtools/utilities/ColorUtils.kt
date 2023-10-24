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
package de.xorg.gsapp.ui.materialtools.utilities

import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.ui.materialtools.annotations.ColorInt
import de.xorg.gsapp.ui.materialtools.annotations.FloatRange
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round

/**
 * Color science utilities.
 *
 *
 * Utility methods for color science constants and color space conversions that aren't HCT or
 * CAM16.
 *
 * @hide
 */
object ColorUtils {
    private val SRGB_TO_XYZ = arrayOf(
        doubleArrayOf(0.41233895, 0.35762064, 0.18051042),
        doubleArrayOf(0.2126, 0.7152, 0.0722),
        doubleArrayOf(0.01932141, 0.11916382, 0.95034478)
    )
    private val XYZ_TO_SRGB = arrayOf(
        doubleArrayOf(
            3.2413774792388685, -1.5376652402851851, -0.49885366846268053
        ), doubleArrayOf(
            -0.9691452513005321, 1.8758853451067872, 0.04156585616912061
        ), doubleArrayOf(
            0.05562093689691305, -0.20395524564742123, 1.0571799111220335
        )
    )
    private val WHITE_POINT_D65 = doubleArrayOf(95.047, 100.0, 108.883)

    /** Converts a color from RGB components to ARGB format.  */
    private fun argbFromRgb(red: Int, green: Int, blue: Int): Int {
        return 255 shl 24 or (red and 255 shl 16) or (green and 255 shl 8) or (blue and 255)
    }

    private fun colorFromRgb(red: Int, green: Int, blue: Int): Color = Color(
        red = red,
        green = green,
        blue = blue,
        alpha = 255
    )

    /** Converts a color from linear RGB components to ARGB format.  */
    @Deprecated("use colorFromLinrgb")
    fun argbFromLinrgb(linrgb: DoubleArray): Int {
        val r = delinearized(linrgb[0])
        val g = delinearized(linrgb[1])
        val b = delinearized(linrgb[2])
        return argbFromRgb(r, g, b)
    }

    /** Converts a color from linear RGB components to compose Color.  */
    fun colorFromLinrgb(linrgb: DoubleArray): Color {
        val r = delinearized(linrgb[0])
        val g = delinearized(linrgb[1])
        val b = delinearized(linrgb[2])
        return colorFromRgb(r, g, b)
    }

    /** Returns the alpha component of a color in ARGB format.  */
    fun alphaFromArgb(argb: Int): Int {
        return argb shr 24 and 255
    }

    /** Returns the red component of a color in ARGB format.  */
    fun redFromArgb(argb: Int): Int {
        return argb shr 16 and 255
    }

    /** Returns the green component of a color in ARGB format.  */
    fun greenFromArgb(argb: Int): Int {
        return argb shr 8 and 255
    }

    /** Returns the blue component of a color in ARGB format.  */
    private fun blueFromArgb(argb: Int): Int {
        return argb and 255
    }

    /** Returns whether a color in ARGB format is opaque.  */
    @Deprecated("use Color.alpha instead", ReplaceWith("isOpaque(argb: Color)"))
    fun isOpaque(argb: Int): Boolean = alphaFromArgb(argb) >= 255

    fun isOpaque(argb: Color): Boolean = argb.alpha >= 255

    /** Converts a color from ARGB to XYZ.  */
    fun argbFromXyz(x: Double, y: Double, z: Double): Int {
        val matrix = XYZ_TO_SRGB
        val linearR = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z
        val linearG = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z
        val linearB = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z
        val r = delinearized(linearR)
        val g = delinearized(linearG)
        val b = delinearized(linearB)
        return argbFromRgb(r, g, b)
    }

    /** Converts a color from XYZ to Color.  */
    fun colorFromXyz(x: Double, y: Double, z: Double): Color {
        val matrix = XYZ_TO_SRGB
        val linearR = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z
        val linearG = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z
        val linearB = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z
        val r = delinearized(linearR)
        val g = delinearized(linearG)
        val b = delinearized(linearB)
        return Color(r, g, b)
    }

    /** Converts a color from XYZ to ARGB.  */
    fun xyzFromArgb(argb: Int): DoubleArray {
        val r = linearized(redFromArgb(argb))
        val g = linearized(greenFromArgb(argb))
        val b = linearized(blueFromArgb(argb))
        return MathUtils.matrixMultiply(doubleArrayOf(r, g, b), SRGB_TO_XYZ)
    }

    fun xyzFromColor(color: Color): DoubleArray {
        val r = linearized(color.red.toInt())
        val g = linearized(color.green.toInt())
        val b = linearized(color.blue.toInt())
        return MathUtils.matrixMultiply(doubleArrayOf(r, g, b), SRGB_TO_XYZ)
    }

    fun scaleBetween(
        inputValue: Float,
        targetMin: Float,
        targetMax: Float,
        inputMin: Float,
        inputMax: Float): Float {
        return (targetMax - targetMin) * (inputValue - inputMin) / (inputMax - inputMin) + targetMin
    }

    /** Converts a color represented in Lab color space into an ARGB integer.  */
    fun argbFromLab(l: Double, a: Double, b: Double): Int {
        val whitePoint = WHITE_POINT_D65
        val fy = (l + 16.0) / 116.0
        val fx = a / 500.0 + fy
        val fz = fy - b / 200.0
        val xNormalized = labInvf(fx)
        val yNormalized = labInvf(fy)
        val zNormalized = labInvf(fz)
        val x = xNormalized * whitePoint[0]
        val y = yNormalized * whitePoint[1]
        val z = zNormalized * whitePoint[2]
        return argbFromXyz(x, y, z)
    }

    /**
     * Converts a color from ARGB representation to L*a*b* representation.
     *
     * @param argb the ARGB representation of a color
     * @return a Lab object representing the color
     */
    fun labFromArgb(argb: Int): DoubleArray {
        val linearR = linearized(redFromArgb(argb))
        val linearG = linearized(greenFromArgb(argb))
        val linearB = linearized(blueFromArgb(argb))
        val matrix = SRGB_TO_XYZ
        val x = matrix[0][0] * linearR + matrix[0][1] * linearG + matrix[0][2] * linearB
        val y = matrix[1][0] * linearR + matrix[1][1] * linearG + matrix[1][2] * linearB
        val z = matrix[2][0] * linearR + matrix[2][1] * linearG + matrix[2][2] * linearB
        val whitePoint = WHITE_POINT_D65
        val xNormalized = x / whitePoint[0]
        val yNormalized = y / whitePoint[1]
        val zNormalized = z / whitePoint[2]
        val fx = labF(xNormalized)
        val fy = labF(yNormalized)
        val fz = labF(zNormalized)
        val l = 116.0 * fy - 16
        val a = 500.0 * (fx - fy)
        val b = 200.0 * (fy - fz)
        return doubleArrayOf(l, a, b)
    }

    /**
     * Converts an L* value to an ARGB representation.
     *
     * @param lstar L* in L*a*b*
     * @return ARGB representation of grayscale color with lightness matching L*
     */
    fun argbFromLstar(lstar: Double): Int {
        val y = yFromLstar(lstar)
        val component = delinearized(y)
        return argbFromRgb(component, component, component)
    }

    /**
     * Computes the L* value of a color in ARGB representation.
     *
     * @param argb ARGB representation of a color
     * @return L*, from L*a*b*, coordinate of the color
     */
    fun lstarFromArgb(argb: Int): Double {
        val y = xyzFromArgb(argb)[1]
        return 116.0 * labF(y / 100.0) - 16.0
    }

    /**
     * Converts an L* value to a Y value.
     *
     *
     * L* in L*a*b* and Y in XYZ measure the same quantity, luminance.
     *
     *
     * L* measures perceptual luminance, a linear scale. Y in XYZ measures relative luminance, a
     * logarithmic scale.
     *
     * @param lstar L* in L*a*b*
     * @return Y in XYZ
     */
    fun yFromLstar(lstar: Double): Double {
        return 100.0 * labInvf((lstar + 16.0) / 116.0)
    }

    /**
     * Converts a Y value to an L* value.
     *
     *
     * L* in L*a*b* and Y in XYZ measure the same quantity, luminance.
     *
     *
     * L* measures perceptual luminance, a linear scale. Y in XYZ measures relative luminance, a
     * logarithmic scale.
     *
     * @param y Y in XYZ
     * @return L* in L*a*b*
     */
    fun lstarFromY(y: Double): Double {
        return labF(y / 100.0) * 116.0 - 16.0
    }

    /**
     * Linearizes an RGB component.
     *
     * @param rgbComponent 0 <= rgb_component <= 255, represents R/G/B channel
     * @return 0.0 <= output <= 100.0, color channel converted to linear RGB space
     */
    fun linearized(rgbComponent: Int): Double {
        val normalized = rgbComponent / 255.0
        return if (normalized <= 0.040449936) {
            normalized / 12.92 * 100.0
        } else {
            ((normalized + 0.055) / 1.055).pow(2.4) * 100
        }
    }

    /**
     * Delinearizes an RGB component.
     *
     * @param rgbComponent 0.0 <= rgb_component <= 100.0, represents linear R/G/B channel
     * @return 0 <= output <= 255, color channel converted to regular RGB space
     */
    fun delinearized(rgbComponent: Double): Int {
        val normalized = rgbComponent / 100.0
        var delinearized = 0.0
        delinearized = if (normalized <= 0.0031308) {
            normalized * 12.92
        } else {
            1.055 * normalized.pow(1.0/2.4) - 0.055
        }
        return MathUtils.clampInt(0, 255, round(delinearized * 255.0).toInt())
    }

    /**
     * Returns the standard white point; white on a sunny day.
     *
     * @return The white point
     */
    fun whitePointD65(): DoubleArray {
        return WHITE_POINT_D65
    }

    fun labF(t: Double): Double {
        val e = 216.0 / 24389.0
        val kappa = 24389.0 / 27.0
        return if (t > e) {
            t.pow(1.0/3.0)
        } else {
            (kappa * t + 16) / 116
        }
    }

    fun labInvf(ft: Double): Double {
        val e = 216.0 / 24389.0
        val kappa = 24389.0 / 27.0
        val ft3 = ft * ft * ft
        return if (ft3 > e) {
            ft3
        } else {
            (116 * ft - 16) / kappa
        }
    }

    fun compositeColors(
        foreground: Color,
        background: Color
    ): Color {
        val bgAlpha: Int = (background.alpha * 255).toInt()
        val fgAlpha: Int = (foreground.alpha * 255).toInt()
        val a: Int = compositeAlpha(fgAlpha, bgAlpha)
        val r: Int = compositeComponent(
            (foreground.red * 255).toInt(), fgAlpha,
            (background.red * 255).toInt(), bgAlpha, a
        )
        val g: Int = compositeComponent(
            (foreground.green * 255).toInt(), fgAlpha,
            (foreground.green * 255).toInt(), bgAlpha, a
        )
        val b: Int = compositeComponent(
            (foreground.blue * 255).toInt(), fgAlpha,
            (background.green * 255).toInt(), bgAlpha, a
        )
        return Color(red = r, green = g, blue = b, alpha = a)
    }

    private fun compositeAlpha(foregroundAlpha: Int, backgroundAlpha: Int): Int {
        return 0xFF - (0xFF - backgroundAlpha) * (0xFF - foregroundAlpha) / 0xFF
    }

    private fun compositeComponent(fgC: Int, fgA: Int, bgC: Int, bgA: Int, a: Int): Int {
        return if (a == 0) 0 else (0xFF * fgC * fgA + bgC * bgA * (0xFF - fgA)) / (a * 0xFF)
    }

    @FloatRange(from = 0.0, to = 1.0)
    fun calculateLuminance(@ColorInt color: Int): Double {
        val result: DoubleArray = xyzFromArgb(color)
        // Luminance is the Y component
        return result[1] / 100
    }

    @FloatRange(from = 0.0, to = 1.0)
    fun calculateLuminance(color: Color): Double {
        val result: DoubleArray = xyzFromColor(color)
        // Luminance is the Y component
        return result[1] / 100
    }


    /**
     * Source: https://stackoverflow.com/a/53095879
     *
     * Converts an HSL color value to RGB. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes h, s, and l are contained in the set [0, 1].
     *
     * @param h       The hue
     * @param s       The saturation
     * @param l       The lightness
     * @return Color
     */
    fun hslToRgb(h: Float, s: Float, l: Float): Color {
        val r: Float
        val g: Float
        val b: Float
        if (s == 0f) {
            b = l
            g = b
            r = g // achromatic
        } else {
            val q = if (l < 0.5f) l * (1 + s) else l + s - l * s
            val p = 2 * l - q
            r = hueToRgb(p, q, h + 1f / 3f)
            g = hueToRgb(p, q, h)
            b = hueToRgb(p, q, h - 1f / 3f)
        }
        return Color((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
    }

    /** Helper method that converts hue to rgb  */
    private fun hueToRgb(p: Float, q: Float, t: Float): Float {
        var t = t
        if (t < 0f) t += 1f
        if (t > 1f) t -= 1f
        if (t < 1f / 6f) return p + (q - p) * 6f * t
        if (t < 1f / 2f) return q
        return if (t < 2f / 3f) p + (q - p) * (2f / 3f - t) * 6f else p
    }

    fun desaturate(input: Color, by: Float): Color {
        val r = input.red / 255
        val g = input.green / 255
        val b = input.blue / 255

        val minV = min(r, min(g, b))
        val maxV = max(r, max(g, b))

        val h: Float = when (maxV) {
            minV -> 0f
            r -> ((60 * (g - b) / (maxV - minV)) + 360) % 360
            g -> (60 * (b - r) / (maxV - minV)) + 120
            b -> (60 * (r - g) / (maxV - minV)) + 240
            else -> 0f
        }

        val l = (maxV + minV) / 2

        val s: Float = if(maxV == minV) 0f
        else if(l <= .5f) ((maxV-minV) / (maxV+minV))
        else ((maxV-minV) / (2-maxV-minV))

        return hslToRgb(h, s*by, l)
    }


}

private fun Float.scale(targetMin: Float,
                        targetMax: Float,
                        inputMin: Float,
                        inputMax: Float): Float {
    return (targetMax - targetMin) * (this - inputMin) / (inputMax - inputMin) + targetMin
    TODO("Not yet implemented")
}
