package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LunaRose,
    onPrimary = Color.White,
    primaryContainer = LunaRoseDeep,
    onPrimaryContainer = Color.White,
    secondary = LunaViolet,
    onSecondary = Color.White,
    tertiary = LunaPeach,
    background = LunaDeepNight,
    onBackground = Color.White,
    surface = LunaDarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF241C3B),
    onSurfaceVariant = Color(0xFFCCC5E0)
)

private val LightColorScheme = lightColorScheme(
    primary = LunaRoseDeep,
    onPrimary = Color.White,
    primaryContainer = LunaPeach,
    onPrimaryContainer = Color(0xFF880E4F),
    secondary = LunaViolet,
    onSecondary = Color.White,
    tertiary = LunaRose,
    background = LunaPearlLight,
    onBackground = Color(0xFF1E1B29),
    surface = Color.White,
    onSurface = Color(0xFF1E1B29),
    surfaceVariant = Color(0xFFF5EBF4),
    onSurfaceVariant = Color(0xFF5A5368)
)

@Composable
fun LunaCycleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
