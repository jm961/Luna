package com.mhss.app.ui.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern circular progress indicator with gradient colors
 */
@Composable
fun ModernLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            strokeWidth = strokeWidth,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Modern pulsing dots loading indicator
 */
@Composable
fun ModernPulsingDots(
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    spacing: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0..2) {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = i * 100,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$i"
            )
            
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(scale)
                    .background(
                        color = dotColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Modern wave loading indicator
 */
@Composable
fun ModernWaveLoader(
    modifier: Modifier = Modifier,
    barWidth: Dp = 4.dp,
    barHeight: Dp = 32.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    spacing: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0..4) {
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = i * 80,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )
            
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight * heightFraction)
                    .background(
                        color = barColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Full screen modern loading overlay
 */
@Composable
fun ModernLoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernLoadingIndicator(size = 56.dp, strokeWidth = 5.dp)
            }
        }
    }
}
