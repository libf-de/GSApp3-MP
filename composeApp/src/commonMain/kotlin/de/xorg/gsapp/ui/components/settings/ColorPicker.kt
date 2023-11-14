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

package de.xorg.gsapp.ui.components.settings


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.asFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.ui.components.HexInputField
import de.xorg.gsapp.ui.tools.ScreenOrientation
import de.xorg.gsapp.ui.tools.toHexString
import kotlin.math.max
import kotlin.math.min


@Composable
fun ColorPicker(
    colorState: MutableState<Color?>,
    orientation: ScreenOrientation,
    maxDim: Int,
    modifier: Modifier = Modifier
) {
    val h = remember { mutableStateOf(colorState.value?.hue() ?: 0f) }
    val s = remember { mutableStateOf(colorState.value?.saturation() ?: 1f) }
    val v = remember { mutableStateOf(colorState.value?.value() ?: 1f) }

    var hex by remember { mutableStateOf(colorState.value.toHexString()) }

    LaunchedEffect(colorState.value) {
        h.value = colorState.value?.hue() ?: 0f
        s.value = colorState.value?.saturation() ?: 1f
        v.value = colorState.value?.value() ?: 1f
    }

    LaunchedEffect(h.value, s.value, v.value) {
        colorState.value = Color.hsv(h.value.coerceIn(0f..360f), s.value, v.value)
        hex = colorState.value.toHexString()
        println(colorState.value.toString())
    }


    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        if(orientation == ScreenOrientation.LANDSCAPE) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.width(maxDim.dp + 52.dp),
            ) {
                SatValPanel(s, v, h.asFloatState(), Modifier.height(maxDim.dp))
                Spacer(Modifier.width(8.dp))
                VerticalHueBar(h, modifier = Modifier.height(maxDim.dp))
            }

            HexInputField(
                colorState
            )
        } else {
            SatValPanel(s, v, h.asFloatState(), modifier.width(maxDim.dp))
            HueBar(h, modifier = Modifier.width(maxDim.dp))
            HexInputField(
                colorState
            )
        }
    }
}






private fun Color.hue(): Float {
    val maxV = max(this.red, max(this.green, this.blue))
    val minV = min(this.red, min(this.green, this.blue))
    val delta = maxV - minV

    return when (maxV) {
        minV -> 0f
        this.red -> (this.green - this.blue) / delta * 60f
        this.green -> ((this.blue - this.red) / delta + 2f) * 60f
        this.blue -> ((this.red - this.green) / delta + 4f) * 60f
        else -> 0f
    }
}

private fun Color.saturation(): Float {
    val maxV = max(this.red, max(this.green, this.blue))
    val minV = min(this.red, min(this.green, this.blue))
    val delta = maxV - minV

    return if (delta == 0f) 0f else delta / maxV
}

private fun Color.value(): Float {
    return max(this.red, max(this.green, this.blue))
}

@Composable
private fun HueBar(
    hueState: MutableState<Float>,
    modifier: Modifier = Modifier,
) {
    var offsetX by remember { mutableStateOf(-1f) }

    Canvas(modifier = modifier.height(52.dp)
        .width(IntrinsicSize.Max)
        .clip(RoundedCornerShape(50))
        .pointerInput(Unit) {
            detectTapGestures {
                offsetX = it.x.coerceIn(0f..size.width.toFloat())
                if(size.width != 0)
                    hueState.value = (offsetX * 360f / size.width).coerceIn(0f..360f)
            }
        }
        .pointerInput(Unit) {
            detectHorizontalDragGestures { change, _ ->
                change.consume()
                offsetX = change.position.x.coerceIn(0.1f..size.width.toFloat())
                if(size.width != 0)
                    hueState.value = (offsetX * 360f / size.width).coerceIn(0f..360f)
            }
        }
    ) {
        if(offsetX < 0)
            offsetX = (hueState.value * size.width / 360f).coerceIn(0f..360f)

        var hue = 0f
        for (i in 0 until size.width.toInt()) {
            drawLine(
                color = Color.hsv(hue = hue,
                                  saturation = 1f,
                                  value = 1f),
                start = Offset(i.toFloat(), 0f),
                end = Offset(i.toFloat(), size.height),
                strokeWidth = 1f
            )

            hue += if(size.width.toInt() == 0) 0f else 360f / size.width.toInt()
        }

        drawCircle(
            color = Color.White,
            radius = size.height / 2,
            center = Offset(offsetX, size.height / 2),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun VerticalHueBar(
    hueState: MutableState<Float>,
    modifier: Modifier = Modifier,
    width: Dp = 52.dp
) {
    var offsetY by remember { mutableStateOf(-1f) }

    Canvas(modifier = modifier.width(width)
        .height(IntrinsicSize.Max)
        .clip(RoundedCornerShape(50))
        .pointerInput(Unit) {
            detectTapGestures {
                offsetY = it.y.coerceIn(0f..size.height.toFloat())
                if(size.height != 0)
                    hueState.value = (offsetY * 360f / size.height).coerceIn(0f..360f)
            }
        }
        .pointerInput(Unit) {
            detectVerticalDragGestures { change, _ ->
                change.consume()
                offsetY = change.position.y.coerceIn(0.1f..size.height.toFloat())
                if(size.height != 0)
                    hueState.value = (offsetY * 360f / size.height).coerceIn(0f..360f)
            }
        }
    ) {
        if(offsetY < 0)
            offsetY = (hueState.value * size.height / 360f).coerceIn(0f..360f)

        var hue = 0f
        for (i in 0 until size.height.toInt()) {
            drawLine(
                color = Color.hsv(hue = hue,
                    saturation = 1f,
                    value = 1f),
                start = Offset(0f, i.toFloat()),
                end = Offset(size.width, i.toFloat()),
                strokeWidth = 1f
            )

            hue += if(size.height.toInt() == 0) 0f else 360f / size.height.toInt()
        }

        drawCircle(
            color = Color.White,
            radius = size.width / 2,
            center = Offset(size.width / 2, offsetY),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}



@Composable
private fun SatValPanel(
    satState: MutableState<Float>,
    valState: MutableState<Float>,
    hueState: FloatState,
    modifier: Modifier = Modifier
) {
    var offset by remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .width(IntrinsicSize.Max)
            .pointerInput(Unit) {
                detectTapGestures {
                    offset = Offset(
                        it.x.coerceIn(0f, size.width.toFloat()),
                        it.y.coerceIn(0f, size.height.toFloat())
                    )

                    satState.value = 1f / size.width * (offset?.x ?: 0f)
                    valState.value = 1f - 1f / size.height * (offset?.y ?: 0f)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()

                    offset = Offset(
                        change.position.x.coerceIn(0f, size.width.toFloat()),
                        change.position.y.coerceIn(0f, size.height.toFloat())
                    )

                    satState.value = 1f / size.width * (offset?.x ?: 0f)
                    valState.value = 1f - 1f / size.height * (offset?.y ?: 0f)
                }
            }
    ) {
        if(offset == null)
            offset = Offset(
                x = size.width * satState.value,
                y = size.height * (1f - valState.value)
            )

        val cr = 12.dp.toPx()  // Corner Radius

        val satBrush = Brush.linearGradient(
            colors = listOf(Color.White, Color.hsv(hueState.value.coerceIn(0f..360f), 1f, 1f)),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            tileMode = TileMode.Clamp
        )

        val valBrush = Brush.linearGradient(
            colors = listOf(Color.White, Color.Black),
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            tileMode = TileMode.Clamp
        )

        drawRoundRect(
            brush = satBrush,
            topLeft = Offset(0f, 0f),
            size = size,
            cornerRadius = CornerRadius(cr, cr),
            /*blendMode = BlendMode.Multiply*/
        )

        drawRoundRect(
            brush = valBrush,
            topLeft = Offset(0f, 0f),
            size = size,
            cornerRadius = CornerRadius(cr, cr),
            blendMode = BlendMode.Multiply
        )

        drawCircle(
            color = Color.hsv(0f, 0f, 1f - valState.value),
            radius = 8.dp.toPx(),
            center = offset!!,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )

        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = offset!!,
        )
    }
}