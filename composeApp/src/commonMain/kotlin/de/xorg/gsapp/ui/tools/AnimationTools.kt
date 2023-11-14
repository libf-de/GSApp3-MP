package de.xorg.gsapp.ui.tools

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.spinAnimation(enabled: Boolean): Modifier {
    /*val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )*/
    // Create an Animatable for rotation
    val angle = remember { Animatable(0f) }
    val rotationSpec = remember { infiniteRepeatable<Float>(
        animation = tween(durationMillis = 1000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    ) }

    // LaunchedEffect to start and stop the rotation animation
    LaunchedEffect(enabled) {
        if (enabled) {
            angle.animateTo(
                targetValue = 360f,
                animationSpec = rotationSpec,
            )
        } else {
            angle.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 0),
            )
        }
    }

    return this.graphicsLayer {
        rotationZ = angle.value
    }
}

private enum class AnimationState {
    Stopped,
    Running,
    Finishing
}