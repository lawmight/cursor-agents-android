package fr.lawmight.cursoragents.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.staticCompositionLocalOf

object MotionDurations {
    const val fast: Int = 120
    const val medium: Int = 200
    const val slow: Int = 320
}

object MotionEasing {
    val standard: Easing = FastOutSlowInEasing
    val emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}

data class MotionTokens(
    val durations: MotionDurations = MotionDurations,
    val easing: MotionEasing = MotionEasing,
)

val LocalMotion = staticCompositionLocalOf { MotionTokens() }
