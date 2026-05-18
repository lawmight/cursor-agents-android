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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.api.models.Target
import fr.lawmight.cursoragents.ui.theme.JetBrainsMonoFamily
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun AgentCard(
    agent: Agent,
    idLabel: String,
    repositoryName: String,
    age: String,
    branchName: String?,
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
                .border(spacing.xxs / 4, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
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
                    modifier = Modifier.size(spacing.s),
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
        Text(
            text = idLabel,
            style =
                MaterialTheme.typography.labelMedium.copy(
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Normal,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(spacing.s))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(spacing.m),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(spacing.xs))
            Text(
                text = repositoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (!branchName.isNullOrBlank()) {
                Text(
                    text = branchName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
        val spacing = LocalSpacing.current
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            AgentCard(
                agent = fixture(status = AgentStatus.RUNNING),
                idLabel = "agent_1",
                repositoryName = "cursor-agents-android",
                age = "2m ago",
                branchName = "cursor/refactor-routes",
            )
            AgentCard(
                agent = fixture(status = AgentStatus.FINISHED, summary = "PR opened: #42"),
                idLabel = "agent_2",
                repositoryName = "cursor-agents-android",
                age = "1h ago",
                branchName = "cursor/open-pr",
            )
            AgentCard(
                agent = fixture(status = AgentStatus.FAILED, summary = "Failed: ktlintCheck"),
                idLabel = "agent_3",
                repositoryName = "cursor-agents-android",
                age = "yesterday",
                branchName = "cursor/ktlint",
            )
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun AgentCardPreviewDark() {
    PreviewSurface(darkTheme = true) {
        val spacing = LocalSpacing.current
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            AgentCard(
                agent = fixture(status = AgentStatus.RUNNING),
                idLabel = "agent_1",
                repositoryName = "cursor-agents-android",
                age = "2m ago",
                branchName = "cursor/refactor-routes",
            )
            AgentCard(
                agent = fixture(status = AgentStatus.FINISHED, summary = "PR opened: #42"),
                idLabel = "agent_2",
                repositoryName = "cursor-agents-android",
                age = "1h ago",
                branchName = "cursor/open-pr",
            )
            AgentCard(
                agent = fixture(status = AgentStatus.FAILED, summary = "Failed: ktlintCheck"),
                idLabel = "agent_3",
                repositoryName = "cursor-agents-android",
                age = "yesterday",
                branchName = "cursor/ktlint",
            )
        }
    }
}
