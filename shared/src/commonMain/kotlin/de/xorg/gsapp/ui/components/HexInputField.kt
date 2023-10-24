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

package de.xorg.gsapp.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.tools.toHexString
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.min

@Composable
fun HexInputField(
    colorState: MutableState<Color?>
) {
    var textValue by remember { mutableStateOf(colorState.value.toHexString()) }
    var validHex by remember { mutableStateOf(false) }

    OutlinedTextField(
        label = { Text(stringResource(MR.strings.dialog_color_hex)) },
        modifier = Modifier.width(IntrinsicSize.Max),
        value = textValue,
        isError = !validHex,
        onValueChange = { str ->
            // Ensure input value starts with # and has at most 6 hex characters
            val it: String = Regex("[#0-9A-Fa-f]+").findAll(str).joinToString(separator = "") { it.value }
            textValue = (
                    if(!it.startsWith("#"))
                        "#" + it.substring(0, min(6, it.length))
                    else
                        it.substring(0, min(7, it.length))
                    ).uppercase()

            validHex = textValue.isValidHexColor()

            if(validHex) {
                try {
                    colorState.value = Color.fromHex(it)
                } catch (_: Exception) { }
            }
        }
    )
}

private fun String.isValidHexColor(): Boolean {
    return Regex("^#(?:[0-9a-fA-F]{3}){1,2}\$").matches(this)
}

private fun Color.Companion.fromHex(str: String): Color {
    return Color(str.removePrefix("#").toLong(16) or 0x00000000FF000000)
}

private fun findFirstDifferencePosition(str1: String, str2: String): Int {
    val minLength = minOf(str1.length, str2.length)

    for (i in 0 until minLength) {
        if (str1[i] != str2[i]) {
            return i
        }
    }

    // Wenn die Schleife bis hierhin durchläuft, bedeutet das, dass die kürzere Zeichenkette am Anfang der längeren Zeichenkette entspricht.
    // Die Position des ersten Unterschieds ist die Länge der kürzeren Zeichenkette.
    return minLength
}