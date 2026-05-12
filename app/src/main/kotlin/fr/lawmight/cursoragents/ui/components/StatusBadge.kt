package fr.lawmight.cursoragents.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.ui.theme.LocalStatusColors
import fr.lawmight.cursoragents.ui.theme.StatusColors

enum class StatusBadgeSize { Small, Medium }

@Composable
fun StatusBadge(
    status: AgentStatus,
    modifier: Modifier = Modifier,
    size: StatusBadgeSize = StatusBadgeSize.Small,
) {
    val palette = LocalStatusColors.current
    val (bg, fg) = palette.colorsFor(status)
    val showPulse = status == AgentStatus.RUNNING || status == AgentStatus.CREATING
    val (paddingH, paddingV, dotSize, fontSp) = when (size) {
        StatusBadgeSize.Small -> StatusMetrics(8.dp, 3.dp, 6.dp, 11)
        StatusBadgeSize.Medium -> StatusMetrics(10.dp, 5.dp, 7.dp, 12)
    }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(bg)
            .padding(horizontal = paddingH, vertical = paddingV),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showPulse) {
            PulsingDot(color = fg, size = dotSize)
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = status.label(),
            color = fg,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = fontSp.sp),
        )
    }
}

private fun StatusColors.colorsFor(status: AgentStatus): Pair<Color, Color> = when (status) {
    AgentStatus.CREATING -> creating to onCreating
    AgentStatus.RUNNING -> running to onRunning
    AgentStatus.FINISHED -> finished to onFinished
    AgentStatus.STOPPED -> stopped to onStopped
    AgentStatus.FAILED -> failed to onFailed
}

private fun AgentStatus.label(): String = when (this) {
    AgentStatus.CREATING -> "Creating"
    AgentStatus.RUNNING -> "Running"
    AgentStatus.FINISHED -> "Finished"
    AgentStatus.STOPPED -> "Stopped"
    AgentStatus.FAILED -> "Failed"
}

private data class StatusMetrics(val padH: Dp, val padV: Dp, val dot: Dp, val fontSp: Int)

@Composable
private fun PulsingDot(color: Color, size: Dp) {
    val transition = rememberInfiniteTransition(label = "status-pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "status-pulse-alpha",
    )
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .alpha(alpha)
            .background(color),
    )
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun StatusBadgePreviewLight() {
    PreviewSurface(darkTheme = false) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AgentStatus.entries.forEach { StatusBadge(it) }
            AgentStatus.entries.forEach { StatusBadge(it, size = StatusBadgeSize.Medium) }
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun StatusBadgePreviewDark() {
    PreviewSurface(darkTheme = true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AgentStatus.entries.forEach { StatusBadge(it) }
            AgentStatus.entries.forEach { StatusBadge(it, size = StatusBadgeSize.Medium) }
        }
    }
}
