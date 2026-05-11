package fr.lawmight.cursoragents.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    background = Bg,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceHover,
    onSurfaceVariant = OnSurfaceMuted,
    error = StatusFailed,
)

private val LightColors = lightColorScheme(
    primary = Accent,
)

data class StatusColors(
    val creating: Color = StatusCreating,
    val running: Color = StatusRunning,
    val finished: Color = StatusFinished,
    val stopped: Color = StatusStopped,
    val failed: Color = StatusFailed,
)

val LocalStatusColors = staticCompositionLocalOf { StatusColors() }

@Composable
fun CursorAgentsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(LocalStatusColors provides StatusColors()) {
        MaterialTheme(colorScheme = scheme, typography = CursorTypography, shapes = CursorShapes, content = content)
    }
}
