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

package de.xorg.gsapp.ui.tools

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.debugInspectorInfo
import kotlin.math.sqrt

/** (CURRENTLY UNUSED, TODO: REMOVE IF NOT NEEDED
 * A modifier that clips the composable content using an animated circle. The circle will
 *  expand/shrink with an animation whenever [visible] changes.
 *
 *  For more fine-grained control over the transition, see this method's overload, which allows passing
 *  a [State] object to control the progress of the reveal animation.
 *
 *  By default, the circle is centered in the content, but custom positions may be specified using
 *  [revealFrom]. Specified offsets should be between 0 (left/top) and 1 (right/bottom).*/
fun Modifier.circularReveal(
    visible: Boolean,
    revealFrom: Offset = Offset(0.5f, 0.5f),
): Modifier = composed(
    factory = {
        val factor = updateTransition(visible, label = "Visibility")
            .animateFloat(label = "revealFactor") { if (it) 1f else 0f }

        circularReveal(factor, revealFrom)
    },
    inspectorInfo = debugInspectorInfo {
        name = "circularReveal"
        properties["visible"] = visible
        properties["revealFrom"] = revealFrom
    }
)

/**A modifier that clips the composable content using a circular shape. The radius of the circle
 * will be determined by the [transitionProgress].
 *
 * The values of the progress should be between 0 and 1.
 *
 * By default, the circle is centered in the content, but custom positions may be specified using
 *  [revealFrom]. Specified offsets should be between 0 (left/top) and 1 (right/bottom).
 *  */
fun Modifier.circularReveal(
    transitionProgress: State<Float>,
    revealFrom: Offset = Offset(0.5f, 0.5f)
): Modifier {
    return drawWithCache {
        val path = Path()

        val center = revealFrom.mapTo(size)
        val radius = calculateRadius(revealFrom, size)

        path.addOval(Rect(center, radius * transitionProgress.value))

        onDrawWithContent {
            clipPath(path) { this@onDrawWithContent.drawContent() }
        }
    }
}

private fun Offset.mapTo(size: Size): Offset {
    return Offset(x * size.width, y * size.height)
}

private fun calculateRadius(normalizedOrigin: Offset, size: Size) = with(normalizedOrigin) {
    val x = (if (x > 0.5f) x else 1 - x) * size.width
    val y = (if (y > 0.5f) y else 1 - y) * size.height

    sqrt(x * x + y * y)
}
