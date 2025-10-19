package com.mhss.app.mybrain.presentation.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhss.app.ui.R
import com.mhss.app.ui.theme.Blue
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun SpaceCard(
    title: String,
    image: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    Card(
        modifier = modifier.scale(scale),
        shape = SquircleShape(
            56.dp,  // Even more rounded!
            CornerSmoothing.Medium
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 16.dp,  // MORE shadow!
            pressedElevation = 12.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Box(
            modifier = contentModifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 1f),      // Solid color at top
                            backgroundColor.copy(alpha = 0.85f),   // Slightly transparent
                            backgroundColor.copy(alpha = 0.7f)     // More transparent at bottom
                        )
                    )
                )
                .aspectRatio(1.0f)
                .padding(24.dp)  // MORE padding!
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(  // BIGGER text!
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,  // BOLDER!
                        letterSpacing = 0.5.sp
                    )
                )
                Image(
                    modifier = Modifier
                        .size(100.dp)  // BIGGER icon!
                        .align(Alignment.End),
                    painter = painterResource(id = image),
                    contentDescription = title
                )
            }
        }
    }
}

@Preview
@Composable
fun SpaceCardPreview() {
    SpaceCard(
        "Notes",
        R.drawable.notes_img,
        Blue
    )
}