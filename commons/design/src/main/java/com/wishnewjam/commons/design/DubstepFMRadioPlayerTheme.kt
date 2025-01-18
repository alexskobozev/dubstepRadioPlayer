package com.wishnewjam.commons.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DubstepFMRadioPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = darkColorPalette(),
    content: @Composable () -> Unit
) {
    // val colors = if (darkTheme) {
    //     darkColorPalette()
    // } else {
    //     lightColorPalette()
    // }
    val colorsPalette = darkColorPalette()
    MaterialTheme(
        colorScheme = colorsPalette,
        content = content
    )
}

private fun darkColorPalette(): ColorScheme {
    return darkColorScheme(
        primary = Color(0xFF1EB980),
        primaryContainer = Color(0xFF1EB980),
        secondary = Color(0xFF1EB980),
        secondaryContainer = Color(0xFF1EB980),
        background = Color(0xFF121212),
        surface = Color(0xFF121212),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
    )
}

private fun lightColorPalette(): ColorScheme {
    return lightColorScheme(
        primary = Color(0xFF1EB980),
        primaryContainer = Color(0xFF1EB980),
        secondary = Color(0xFF1EB980),
        secondaryContainer = Color(0xFF1EB980),
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
    )
}
