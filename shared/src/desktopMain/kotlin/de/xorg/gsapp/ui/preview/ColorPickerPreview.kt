package de.xorg.gsapp.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.xorg.gsapp.ui.components.settings.ColorPicker
import de.xorg.gsapp.ui.tools.ScreenOrientation

@Composable
@Preview
fun ColorPickerPreview() {
    //HueBar(setColor = { value -> })
    var colorState = remember { mutableStateOf<Color?>(Color.Green)}

    ColorPicker(
        colorState = colorState,
        orientation = ScreenOrientation.LANDSCAPE,
        maxDim = 300,
        modifier = Modifier.width(300.dp))
}