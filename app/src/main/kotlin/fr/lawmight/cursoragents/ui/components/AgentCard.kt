package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.data.api.Agent
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.data.api.Source
import fr.lawmight.cursoragents.data.api.Target
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun AgentCard(
    agent: Agent,
    age: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val spacing = LocalSpacing.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
                .clickable(onClick = onClick)
                .padding(spacing.m),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatusBadge(status = agent.status)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(spacing.xxs))
                Text(
                    text = age,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(spacing.s))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(spacing.xs))
            Text(
                text = agent.source.repository.removePrefix("https://github.com/"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (agent.source.ref != null) {
                Text(
                    text = "  ·  ${agent.source.ref}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (!agent.summary.isNullOrBlank()) {
            Spacer(Modifier.height(spacing.s))
            Text(
                text = agent.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun fixture(
    id: String = "agent_1",
    status: AgentStatus = AgentStatus.RUNNING,
    summary: String? = "Refactor the navigation host to use type-safe routes",
): Agent =
    Agent(
        id = id,
        name = "",
        status = status,
        source = Source(repository = "https://github.com/lawmight/cursor-agents-android", ref = "main"),
        target = Target(branchName = "cursor/refactor-routes"),
        summary = summary,
        createdAt = "2026-05-12T08:00:00Z",
    )

@Preview(name = "Light", widthDp = 360)
@Composable
private fun AgentCardPreviewLight() {
    PreviewSurface(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AgentCard(agent = fixture(status = AgentStatus.RUNNING), age = "2m ago")
            AgentCard(agent = fixture(status = AgentStatus.FINISHED, summary = "PR opened: #42"), age = "1h ago")
            AgentCard(agent = fixture(status = AgentStatus.FAILED, summary = "Failed: ktlintCheck"), age = "yesterday")
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun AgentCardPreviewDark() {
    PreviewSurface(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AgentCard(agent = fixture(status = AgentStatus.RUNNING), age = "2m ago")
            AgentCard(agent = fixture(status = AgentStatus.FINISHED, summary = "PR opened: #42"), age = "1h ago")
            AgentCard(agent = fixture(status = AgentStatus.FAILED, summary = "Failed: ktlintCheck"), age = "yesterday")
        }
    }
}
