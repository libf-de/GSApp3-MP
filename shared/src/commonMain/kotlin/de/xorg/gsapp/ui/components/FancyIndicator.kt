/*
 * This file was cherry-picked from MensaApp (https://github.com/mensa-app-wuerzburg/Android)
 * Used with permission.
 * Copyright (C) 2023 Erik Spall
 */

package de.xorg.gsapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FancyIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = color
    ) {

    }
}