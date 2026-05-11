package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.ui.theme.LocalStatusColors

@Composable
fun StatusBadge(status: AgentStatus, modifier: Modifier = Modifier) {
    val palette = LocalStatusColors.current
    val color: Color = when (status) {
        AgentStatus.CREATING -> palette.creating
        AgentStatus.RUNNING -> palette.running
        AgentStatus.FINISHED -> palette.finished
        AgentStatus.STOPPED -> palette.stopped
        AgentStatus.FAILED -> palette.failed
    }
    Text(
        text = status.name,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
