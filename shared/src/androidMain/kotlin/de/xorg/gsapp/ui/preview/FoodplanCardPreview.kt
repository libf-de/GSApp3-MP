package de.xorg.gsapp.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.ui.components.FoodplanCard

@Composable
@Preview
fun FoodplanCardPreview() {
    FoodplanCard(
        food = Food(
            num = 1,
            name = "Spaghetti Bolognese",
            additives = listOf("a", "b")
        ),
        color = Color.Green
    )
}