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

internal val DarkStatusColors =
    StatusColors(
        creating = StatusCreating,
        onCreating = StatusCreatingDarkOn,
        running = StatusRunning,
        onRunning = StatusRunningDarkOn,
        finished = StatusFinished,
        onFinished = StatusFinishedDarkOn,
        stopped = StatusStopped,
        onStopped = StatusStoppedDarkOn,
        failed = StatusFailed,
        onFailed = StatusFailedDarkOn,
    )

internal val LightStatusColors =
    StatusColors(
        creating = StatusCreating,
        onCreating = StatusCreatingLightOn,
        running = StatusRunning,
        onRunning = StatusRunningLightOn,
        finished = StatusFinished,
        onFinished = StatusFinishedLightOn,
        stopped = StatusStopped,
        onStopped = StatusStoppedLightOn,
        failed = StatusFailed,
        onFailed = StatusFailedLightOn,
    )

val LocalStatusColors = staticCompositionLocalOf { DarkStatusColors }

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
