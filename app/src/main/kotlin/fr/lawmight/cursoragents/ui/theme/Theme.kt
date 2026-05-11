package fr.lawmight.cursoragents.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun CursorAgentsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) CursorDarkColorScheme else CursorLightColorScheme
    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors
    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalMotion provides MotionTokens(),
        LocalStatusColors provides statusColors,
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = CursorTypography,
            shapes = CursorShapes,
            content = content,
        )
    }
}
