package fr.lawmight.cursoragents.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColors =
    darkColorScheme(
        primary = Accent,
        onPrimary = OnAccent,
        primaryContainer = AccentPressed,
        onPrimaryContainer = OnAccent,
        secondary = AccentMuted,
        onSecondary = OnAccent,
        tertiary = StatusFinished,
        onTertiary = StatusFinishedDarkOn,
        background = DarkBg,
        onBackground = DarkOnSurface,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceMuted,
        surfaceTint = Accent,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant,
        error = DarkError,
        onError = Color.White,
        inverseSurface = LightSurface,
        inverseOnSurface = LightOnSurface,
    )

private val LightColors =
    lightColorScheme(
        primary = Accent,
        onPrimary = OnAccent,
        primaryContainer = AccentMuted,
        onPrimaryContainer = LightOnSurface,
        secondary = AccentPressed,
        onSecondary = OnAccent,
        tertiary = StatusFinished,
        onTertiary = StatusFinishedLightOn,
        background = LightBg,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceMuted,
        surfaceTint = Accent,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant,
        error = LightError,
        onError = Color.White,
        inverseSurface = DarkSurface,
        inverseOnSurface = DarkOnSurface,
    )

data class StatusColors(
    val creating: Color,
    val onCreating: Color,
    val running: Color,
    val onRunning: Color,
    val finished: Color,
    val onFinished: Color,
    val stopped: Color,
    val onStopped: Color,
    val failed: Color,
    val onFailed: Color,
)

private fun statusColorsFor(scheme: androidx.compose.material3.ColorScheme): StatusColors =
    StatusColors(
        creating = scheme.secondary,
        onCreating = scheme.onSecondary,
        running = scheme.primary,
        onRunning = scheme.onPrimary,
        finished = scheme.tertiary,
        onFinished = scheme.onTertiary,
        stopped = scheme.surfaceVariant,
        onStopped = scheme.onSurfaceVariant,
        failed = scheme.error,
        onFailed = scheme.onError,
    )

internal val DarkStatusColors = statusColorsFor(DarkColors)

internal val LightStatusColors = statusColorsFor(LightColors)

val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }

@Composable
fun CursorAgentsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColors else LightColors
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
