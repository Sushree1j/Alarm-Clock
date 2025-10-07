package com.example.securealarm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
)

@Composable
fun SecureAlarmTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
