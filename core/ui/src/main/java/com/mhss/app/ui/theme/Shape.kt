package com.mhss.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Small components: chips, buttons, text fields
    small = RoundedCornerShape(12.dp),
    
    // Medium components: cards, dialogs
    medium = RoundedCornerShape(16.dp),
    
    // Large components: bottom sheets, large cards
    large = RoundedCornerShape(24.dp),
    
    // Extra large: special components
    extraLarge = RoundedCornerShape(32.dp)
)