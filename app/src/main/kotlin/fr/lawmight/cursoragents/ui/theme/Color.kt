package fr.lawmight.cursoragents.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import fr.lawmight.cursoragents.data.api.AgentStatus

val CursorBlack = Color(0xFF050507)
val CursorWhite = Color(0xFFFFFFFF)

val CursorGray50 = Color(0xFFFAFAFB)
val CursorGray100 = Color(0xFFF2F3F5)
val CursorGray200 = Color(0xFFE4E6EA)
val CursorGray300 = Color(0xFFCDD0D8)
val CursorGray400 = Color(0xFFAEB4C0)
val CursorGray500 = Color(0xFF8E95A3)
val CursorGray600 = Color(0xFF6F7785)
val CursorGray700 = Color(0xFF545B68)
val CursorGray800 = Color(0xFF383E49)
val CursorGray900 = Color(0xFF1F2430)
val CursorGray950 = Color(0xFF0D0E12)

val CursorAccent50 = Color(0xFFF1F2FF)
val CursorAccent100 = Color(0xFFE1E4FF)
val CursorAccent200 = Color(0xFFC7CCFF)
val CursorAccent300 = Color(0xFFA8B0FF)
val CursorAccent400 = Color(0xFF8791F2)
val CursorAccent500 = Color(0xFF6F7AE3)
val CursorAccent600 = Color(0xFF5E6AD2)
val CursorAccent700 = Color(0xFF4C56B8)
val CursorAccent800 = Color(0xFF3D4594)
val CursorAccent900 = Color(0xFF30376F)

val CursorLightSurface = CursorGray50
val CursorLightSurfaceVariant = CursorGray100
val CursorLightSurfaceContainer = CursorWhite
val CursorLightOnSurface = CursorGray950
val CursorLightOnSurfaceVariant = CursorGray700
val CursorLightPrimary = CursorAccent600
val CursorLightOnPrimary = CursorWhite
val CursorLightPrimaryContainer = CursorAccent100
val CursorLightOnPrimaryContainer = CursorAccent900
val CursorLightOutline = CursorGray500
val CursorLightOutlineVariant = CursorGray200
val CursorLightError = Color(0xFFD92D20)
val CursorLightOnError = CursorWhite
val CursorLightSuccess = Color(0xFF16803C)
val CursorLightOnSuccess = CursorWhite
val CursorLightWarning = Color(0xFFA15C00)
val CursorLightOnWarning = CursorWhite

val CursorDarkSurface = CursorGray950
val CursorDarkSurfaceVariant = CursorGray900
val CursorDarkSurfaceContainer = Color(0xFF11131A)
val CursorDarkOnSurface = CursorGray50
val CursorDarkOnSurfaceVariant = CursorGray400
val CursorDarkPrimary = CursorAccent400
val CursorDarkOnPrimary = CursorGray950
val CursorDarkPrimaryContainer = CursorAccent800
val CursorDarkOnPrimaryContainer = CursorAccent50
val CursorDarkOutline = CursorGray600
val CursorDarkOutlineVariant = CursorGray800
val CursorDarkError = Color(0xFFFF6B66)
val CursorDarkOnError = CursorGray950
val CursorDarkSuccess = Color(0xFF44C285)
val CursorDarkOnSuccess = CursorGray950
val CursorDarkWarning = Color(0xFFE0B341)
val CursorDarkOnWarning = CursorGray950

val CursorLightColorScheme: ColorScheme =
    lightColorScheme(
        primary = CursorLightPrimary,
        onPrimary = CursorLightOnPrimary,
        primaryContainer = CursorLightPrimaryContainer,
        onPrimaryContainer = CursorLightOnPrimaryContainer,
        background = CursorLightSurface,
        onBackground = CursorLightOnSurface,
        surface = CursorLightSurface,
        onSurface = CursorLightOnSurface,
        surfaceVariant = CursorLightSurfaceVariant,
        onSurfaceVariant = CursorLightOnSurfaceVariant,
        surfaceContainer = CursorLightSurfaceContainer,
        outline = CursorLightOutline,
        outlineVariant = CursorLightOutlineVariant,
        error = CursorLightError,
        onError = CursorLightOnError,
    )

val CursorDarkColorScheme: ColorScheme =
    darkColorScheme(
        primary = CursorDarkPrimary,
        onPrimary = CursorDarkOnPrimary,
        primaryContainer = CursorDarkPrimaryContainer,
        onPrimaryContainer = CursorDarkOnPrimaryContainer,
        background = CursorDarkSurface,
        onBackground = CursorDarkOnSurface,
        surface = CursorDarkSurface,
        onSurface = CursorDarkOnSurface,
        surfaceVariant = CursorDarkSurfaceVariant,
        onSurfaceVariant = CursorDarkOnSurfaceVariant,
        surfaceContainer = CursorDarkSurfaceContainer,
        outline = CursorDarkOutline,
        outlineVariant = CursorDarkOutlineVariant,
        error = CursorDarkError,
        onError = CursorDarkOnError,
    )

data class StatusColor(
    val foreground: Color,
    val background: Color,
)

data class StatusColors(
    val creatingForeground: Color,
    val creatingBackground: Color,
    val runningForeground: Color,
    val runningBackground: Color,
    val finishedForeground: Color,
    val finishedBackground: Color,
    val stoppedForeground: Color,
    val stoppedBackground: Color,
    val failedForeground: Color,
    val failedBackground: Color,
) {
    val creating: Color = creatingBackground
    val running: Color = runningBackground
    val finished: Color = finishedBackground
    val stopped: Color = stoppedBackground
    val failed: Color = failedBackground

    fun forStatus(status: AgentStatus): StatusColor =
        when (status) {
            AgentStatus.CREATING -> StatusColor(creatingForeground, creatingBackground)
            AgentStatus.RUNNING -> StatusColor(runningForeground, runningBackground)
            AgentStatus.FINISHED -> StatusColor(finishedForeground, finishedBackground)
            AgentStatus.STOPPED -> StatusColor(stoppedForeground, stoppedBackground)
            AgentStatus.FAILED -> StatusColor(failedForeground, failedBackground)
        }
}

val LightStatusColors =
    StatusColors(
        creatingForeground = Color(0xFF7A4B00),
        creatingBackground = Color(0xFFFFF2CC),
        runningForeground = Color(0xFF0B5CAD),
        runningBackground = Color(0xFFDDEEFF),
        finishedForeground = Color(0xFF0F6B35),
        finishedBackground = Color(0xFFDFF7E9),
        stoppedForeground = CursorGray700,
        stoppedBackground = CursorGray200,
        failedForeground = Color(0xFFB42318),
        failedBackground = Color(0xFFFFE3E0),
    )

val DarkStatusColors =
    StatusColors(
        creatingForeground = Color(0xFFFFE3A3),
        creatingBackground = Color(0xFF3D2E0A),
        runningForeground = Color(0xFFA8D4FF),
        runningBackground = Color(0xFF102A43),
        finishedForeground = Color(0xFFA7F3C4),
        finishedBackground = Color(0xFF10351F),
        stoppedForeground = CursorGray300,
        stoppedBackground = Color(0xFF2A2F3A),
        failedForeground = Color(0xFFFFB4AD),
        failedBackground = Color(0xFF3F1516),
    )

val LocalStatusColors = staticCompositionLocalOf { DarkStatusColors }
