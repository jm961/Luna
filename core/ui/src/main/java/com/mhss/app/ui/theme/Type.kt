package com.mhss.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mhss.app.ui.R

val Rubik = FontFamily(
    Font(R.font.rubik_regular),
    Font(R.font.rubik_bold, FontWeight.Bold)
)

fun getTypography(font: FontFamily, fontSizeScale: Float = 1.0f) = Typography(
    // Display styles - For large, impactful text
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = (57 * fontSizeScale).sp,
        lineHeight = (64 * fontSizeScale).sp,
        letterSpacing = (-0.25 * fontSizeScale).sp,
        fontFamily = font
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = (45 * fontSizeScale).sp,
        lineHeight = (52 * fontSizeScale).sp,
        letterSpacing = 0.sp,
        fontFamily = font
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = (36 * fontSizeScale).sp,
        lineHeight = (44 * fontSizeScale).sp,
        letterSpacing = 0.sp,
        fontFamily = font
    ),
    
    // Headline styles - For section headers
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = (32 * fontSizeScale).sp,
        lineHeight = (40 * fontSizeScale).sp,
        letterSpacing = 0.sp,
        fontFamily = font
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = (28 * fontSizeScale).sp,
        lineHeight = (36 * fontSizeScale).sp,
        letterSpacing = 0.sp,
        fontFamily = font
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = (24 * fontSizeScale).sp,
        lineHeight = (32 * fontSizeScale).sp,
        letterSpacing = 0.sp,
        fontFamily = font
    ),
    
    // Title styles - For card titles and important elements
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = (22 * fontSizeScale).sp,
        lineHeight = (28 * fontSizeScale).sp,
        letterSpacing = 0.sp,
        fontFamily = font
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = (16 * fontSizeScale).sp,
        lineHeight = (24 * fontSizeScale).sp,
        letterSpacing = (0.15 * fontSizeScale).sp,
        fontFamily = font
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = (14 * fontSizeScale).sp,
        lineHeight = (20 * fontSizeScale).sp,
        letterSpacing = (0.1 * fontSizeScale).sp,
        fontFamily = font
    ),
    
    // Body styles - For main content
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = (16 * fontSizeScale).sp,
        lineHeight = (24 * fontSizeScale).sp,
        letterSpacing = (0.5 * fontSizeScale).sp,
        fontFamily = font
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = (14 * fontSizeScale).sp,
        lineHeight = (20 * fontSizeScale).sp,
        letterSpacing = (0.25 * fontSizeScale).sp,
        fontFamily = font
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = (12 * fontSizeScale).sp,
        lineHeight = (16 * fontSizeScale).sp,
        letterSpacing = (0.4 * fontSizeScale).sp,
        fontFamily = font
    ),
    
    // Label styles - For buttons and input fields
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = (14 * fontSizeScale).sp,
        lineHeight = (20 * fontSizeScale).sp,
        letterSpacing = (0.1 * fontSizeScale).sp,
        fontFamily = font
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = (12 * fontSizeScale).sp,
        lineHeight = (16 * fontSizeScale).sp,
        letterSpacing = (0.5 * fontSizeScale).sp,
        fontFamily = font
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = (11 * fontSizeScale).sp,
        lineHeight = (16 * fontSizeScale).sp,
        letterSpacing = (0.5 * fontSizeScale).sp,
        fontFamily = font
    )
)