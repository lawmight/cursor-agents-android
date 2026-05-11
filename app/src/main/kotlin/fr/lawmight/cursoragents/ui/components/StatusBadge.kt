package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.ui.theme.LocalSpacing
import fr.lawmight.cursoragents.ui.theme.LocalStatusColors

@Composable
fun StatusBadge(status: AgentStatus, modifier: Modifier = Modifier) {
    val colors = LocalStatusColors.current.forStatus(status)
    val spacing = LocalSpacing.current
    Text(
        text = status.name,
        style = MaterialTheme.typography.labelSmall,
        color = colors.foreground,
        modifier = modifier
            .clip(CircleShape)
            .background(colors.background)
            .padding(horizontal = spacing.xs, vertical = spacing.xxs),
    )
}
