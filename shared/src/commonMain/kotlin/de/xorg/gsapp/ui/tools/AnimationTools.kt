package de.xorg.gsapp.ui.tools

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.spinAnimation(enabled: Boolean): Modifier {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    return this.graphicsLayer {
        rotationZ = if(enabled || angle in 355F .. 360F) {
            angle
        } else {
            0F
        }
    }
}

private enum class AnimationState {
    Stopped,
    Running,
    Finishing
}