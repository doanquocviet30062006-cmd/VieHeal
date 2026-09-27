package com.vieheal.mobile.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
    lightColorScheme(
        primary = VieHealPrimaryLight,
        onPrimary = VieHealOnPrimaryLight,
        primaryContainer = VieHealPrimaryContainerLight,
        onPrimaryContainer = VieHealOnPrimaryContainerLight,
        secondary = VieHealSecondaryLight,
        background = VieHealBackgroundLight,
        surface = VieHealSurfaceLight,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = VieHealPrimaryDark,
        onPrimary = VieHealOnPrimaryDark,
        primaryContainer = VieHealPrimaryContainerDark,
        onPrimaryContainer = VieHealOnPrimaryContainerDark,
        secondary = VieHealSecondaryDark,
        background = VieHealBackgroundDark,
        surface = VieHealSurfaceDark,
    )

@Composable
fun VieHealTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        shapes = VieHealShapes,
        content = content,
    )
}
