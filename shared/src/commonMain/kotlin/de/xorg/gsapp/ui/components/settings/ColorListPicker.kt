package de.xorg.gsapp.ui.components.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColorListPicker(
    colorState: MutableState<Color?>,
    colors: List<Color> = DEFAULT_COLORS,
    itemSize: Dp = 48.dp
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(itemSize)
    ) {
        val itemModifier = Modifier.padding(4.dp).size(itemSize).aspectRatio(1f)

        items(colors) {
            ColorItem(
                color = it,
                onClick = { color -> colorState.value = color },
                isSelected = colorState.value == it,
                modifier = itemModifier
            )
        }
    }
}

@Composable
fun ColorListPicker(
    onColorSelected: (Color) -> Unit,
    colors: List<Color> = DEFAULT_COLORS,
    itemSize: Dp = 48.dp
) {
    var selectedColor by remember { mutableStateOf<Color?>(null) }

    LaunchedEffect(selectedColor) {
        if(selectedColor != null)
            onColorSelected(selectedColor!!)
    }

    LazyVerticalGrid(
        columns = GridCells.FixedSize(56.dp)
    ) {
        val itemModifier = Modifier.size(itemSize)

        items(colors) {
            ColorItem(
                color = it,
                onClick = { selectedColor = it },
                isSelected = selectedColor == it,
                modifier = itemModifier
            )
        }
    }
}

val DEFAULT_COLORS = listOf(
    Color(0xFFFF7F7F),
    Color(0xFFFF0000),
    Color(0xFF990000),
    Color(0xFFB27300),
    Color(0xFFFF8F00),
    Color(0xFFFFC04C),
    Color(0xFFB2B200),
    Color(0xFFFFFF00),
    Color(0xFFFFFF66),
    Color(0xFF7FFF7F),
    Color(0xFF00FF00),
    Color(0xFF007f00),
    Color(0xFF2B7D7A),
    Color(0xFF48D1CC),
    Color(0xFFA3E8E5),
    Color(0xFFCCFFFF),
    Color(0xFF00FFFF),
    Color(0xFF009999),
    Color(0xFF000066),
    Color(0xFF0000FF),
    Color(0xFFCCCCFF),
    Color(0xFFD2C1E2),
    Color(0xFF6A329F),
    Color(0xFF4A236F),
    Color(0xFF741B47),
    Color(0xFFB2296D),
    Color(0xFFEBA8C9),
    Color(0xFFFFB2FF),
    Color(0xFFFF00FF),
    Color(0xFF660066),
    Color(0xFF521515),
    Color(0xFF523415),
    Color(0xFFA5682A),
    Color(0xFFDBC2A9),
    Color(0xFFFFFFFF),
    Color(0xFFAAAAAA),
    Color(0xFF000000))