package com.mhss.app.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Creates a modern shimmer effect for loading states
 */
@Composable
fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color(0xFFB8B5B5),
        Color(0xFF8F8B8B),
        Color(0xFFB8B5B5),
    )
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(translateAnimation, translateAnimation),
            end = Offset(translateAnimation + 200f, translateAnimation + 200f)
        )
    )
}

/**
 * Creates a modern gradient background
 */
fun Modifier.gradientBackground(
    colors: List<Color>,
    angleInDegrees: Float = 45f
): Modifier = this.then(
    Modifier.background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset.Zero,
            end = Offset(
                x = cos(angleInDegrees * PI / 180).toFloat() * 1000f,
                y = sin(angleInDegrees * PI / 180).toFloat() * 1000f
            )
        )
    )
)

/**
 * Creates a modern glass morphism effect
 */
fun Modifier.glassMorphism(
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    blurRadius: Dp = 10.dp
): Modifier = this.then(
    Modifier.background(
        color = backgroundColor,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    )
)

/**
 * Animated gradient rotation effect
 */
@Composable
fun Modifier.animatedGradientBorder(
    colors: List<Color>,
    strokeWidth: Dp = 2.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_border")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    drawBehind {
        rotate(angle) {
            drawCircle(
                brush = Brush.sweepGradient(colors),
                radius = size.minDimension / 2,
                center = center
            )
        }
    }
}
