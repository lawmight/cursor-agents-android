package fr.lawmight.cursoragents.ui.theme

import androidx.compose.ui.graphics.Color

internal val Accent = Color(0xFF5E6AD2) // Linear-inspired indigo
internal val AccentPressed = Color(0xFF4F5BC0)
internal val AccentMuted = Color(0xFF8088DC)
internal val OnAccent = Color(0xFFFFFFFF)

internal val DarkBg = Color(0xFF0A0A0A)
internal val DarkSurface = Color(0xFF111114)
internal val DarkSurfaceVariant = Color(0xFF1A1A1F)
internal val DarkSurfaceHover = Color(0xFF22222A)
internal val DarkOnSurface = Color(0xFFEDEDED)
internal val DarkOnSurfaceMuted = Color(0xFF8A8A93)
internal val DarkOutline = Color(0xFF2A2A33)
internal val DarkOutlineVariant = Color(0xFF1F1F26)
internal val DarkError = Color(0xFFE5484D)

internal val LightBg = Color(0xFFFAFAFA)
internal val LightSurface = Color(0xFFFFFFFF)
internal val LightSurfaceVariant = Color(0xFFF2F2F5)
internal val LightSurfaceHover = Color(0xFFE9E9EE)
internal val LightOnBackground = Color(0xFF0A0A0A)
internal val LightOnSurface = Color(0xFF171717)
internal val LightOnSurfaceMuted = Color(0xFF5C5C66)
internal val LightOutline = Color(0xFFE5E5EA)
internal val LightOutlineVariant = Color(0xFFEFEFF2)
internal val LightError = Color(0xFFC42B2B)

// Status palette (5 states per Cursor API). Foregrounds picked to clear WCAG AA on the
// matching background color. STOPPED's gray fails on white text in light mode, so we
// darken it specifically for light surfaces.
internal val StatusCreating = Color(0xFFE0B341)
internal val StatusRunning = Color(0xFF4FA8FF)
internal val StatusFinished = Color(0xFF44C285)
internal val StatusStopped = Color(0xFF8A8A93)
internal val StatusFailed = Color(0xFFE5484D)

internal val StatusCreatingDarkOn = Color(0xFF2A1F00)
internal val StatusRunningDarkOn = Color(0xFF002645)
internal val StatusFinishedDarkOn = Color(0xFF00321A)
internal val StatusStoppedDarkOn = Color(0xFFFFFFFF)
internal val StatusFailedDarkOn = Color(0xFFFFFFFF)

internal val StatusCreatingLightOn = Color(0xFF2A1F00)
internal val StatusRunningLightOn = Color(0xFFFFFFFF)
internal val StatusFinishedLightOn = Color(0xFF00321A)
internal val StatusStoppedLightOn = Color(0xFF1A1A1F)
internal val StatusFailedLightOn = Color(0xFFFFFFFF)
