package com.mhss.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

private val DarkColorPalette = darkColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryDark,
    onPrimary = OnPrimary,
    onPrimaryContainer = Color.White,
    secondary = SecondaryColor,
    secondaryContainer = SecondaryLight,
    tertiary = TertiaryColor,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    surfaceContainerHighest = DarkSurfaceVariant,
    surfaceContainerLow = DarkSurface,
    surfaceContainerLowest = DarkGray,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceVariant,
    surfaceDim = DarkGray,
    surfaceBright = DarkSurfaceVariant,
    background = DarkGray,
    onSurface = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFFD1D5DB),
    onBackground = Color(0xFFE5E7EB),
    surfaceTint = PrimaryColor,
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF4B5563)
)

private val LightColorPalette = lightColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryLight,
    onPrimary = OnPrimary,
    onPrimaryContainer = DarkText,
    secondary = SecondaryColor,
    secondaryContainer = SecondaryLight,
    tertiary = TertiaryColor,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    surfaceVariant = Color(0xFFF9FAFB),
    surfaceContainerHighest = LightGray,
    surfaceContainerLow = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainer = Color(0xFFF9FAFB),
    surfaceContainerHigh = LightGray,
    surfaceDim = Color(0xFFE5E7EB),
    surfaceBright = Color.White,
    onSurface = DarkText,
    onSurfaceVariant = Color(0xFF6B7280),
    onBackground = DarkText,
    surfaceTint = PrimaryColor,
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB)
)

@Composable
fun MyBrainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColors: Boolean = false,
    fontFamily: FontFamily = Rubik,
    fontSizeScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = if (useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) {
            dynamicDarkColorScheme(context)
        } else dynamicLightColorScheme(context)
    } else if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val typography = getTypography(fontFamily, fontSizeScale)
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}