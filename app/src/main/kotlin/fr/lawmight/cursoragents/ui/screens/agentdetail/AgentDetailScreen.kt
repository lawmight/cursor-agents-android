package fr.lawmight.cursoragents.ui.screens.agentdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.api.models.Target
import fr.lawmight.cursoragents.ui.components.EmptyState
import fr.lawmight.cursoragents.ui.components.ErrorState
import fr.lawmight.cursoragents.ui.components.GhostIconButton
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.PromptField
import fr.lawmight.cursoragents.ui.components.SectionHeader
import fr.lawmight.cursoragents.ui.components.ShimmerBlock
import fr.lawmight.cursoragents.ui.components.StatusBadge
import fr.lawmight.cursoragents.ui.components.StatusBadgeSize
import fr.lawmight.cursoragents.ui.theme.LocalSpacing
import java.time.Instant

@Composable
fun AgentDetailScreen(
    onBack: () -> Unit,
    viewModel: AgentDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AgentDetailContent(
        state = state,
        onBack = onBack,
        onFollowupChange = viewModel::onFollowupChange,
        onSendFollowup = viewModel::sendFollowup,
        onRefresh = viewModel::refresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentDetailContent(
    state: AgentDetailUiState,
    onBack: () -> Unit,
    onFollowupChange: (String) -> Unit,
    onSendFollowup: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AgentTitle(state = state)
                },
                navigationIcon = {
                    GhostIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBack,
                        contentDescription = stringResource(R.string.detail_back),
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        bottomBar = {
            if (state is AgentDetailUiState.Content) {
                FollowUpBar(
                    state = state,
                    onValueChange = onFollowupChange,
                    onSend = { onSendFollowup(state.followupText) },
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(pad),
        ) {
            when (state) {
                AgentDetailUiState.Loading -> LoadingContent()
                is AgentDetailUiState.Error ->
                    ErrorState(
                        title = stringResource(R.string.detail_error_title),
                        body = state.message,
                        retryLabel = stringResource(R.string.agents_retry),
                        onRetry = onRefresh,
                    )
                is AgentDetailUiState.Content ->
                    LoadedContent(
                        state = state,
                        onRefresh = onRefresh,
                        contentPadding = PaddingValues(bottom = spacing.l),
                    )
            }
        }
    }
}

@Composable
private fun AgentTitle(state: AgentDetailUiState) {
    val title =
        when (state) {
            AgentDetailUiState.Loading -> stringResource(R.string.detail_loading_title)
            is AgentDetailUiState.Error -> stringResource(R.string.detail_error_title)
            is AgentDetailUiState.Content -> state.agent.source.repository.repositoryName()
        }
    val subtitle =
        (state as? AgentDetailUiState.Content)
            ?.agent
            ?.displayBranch()
            .orEmpty()

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedContent(
    state: AgentDetailUiState.Content,
    onRefresh: () -> Unit,
    contentPadding: PaddingValues,
) {
    val spacing = LocalSpacing.current
    val listState = rememberLazyListState()
    val pullState = rememberPullToRefreshState()

    LaunchedEffect(state.turns.size) {
        if (state.turns.isNotEmpty()) {
            listState.animateScrollToItem(state.turns.lastIndex + CONVERSATION_HEADER_ITEMS)
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        state = pullState,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            item {
                StatusHeader(state = state)
            }
            item {
                AgentMeta(agent = state.agent)
            }
            if (state.refreshError != null) {
                item {
                    InlineError(text = state.refreshError)
                }
            }
            item {
                SectionHeader(text = stringResource(R.string.detail_conversation))
            }
            if (state.turns.isEmpty()) {
                item {
                    EmptyState(
                        title = stringResource(R.string.detail_no_messages_title),
                        body = stringResource(R.string.detail_no_messages_body),
                    )
                }
            } else {
                items(items = state.turns, key = { it.id }) { turn ->
                    ConversationTurnCard(turn = turn)
                }
            }
        }
    }
}

@Composable
private fun StatusHeader(state: AgentDetailUiState.Content) {
    val spacing = LocalSpacing.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.m, vertical = spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        StatusBadge(status = state.agent.status, size = StatusBadgeSize.Medium)
        if (state.agent.status == AgentStatus.RUNNING || state.agent.status == AgentStatus.CREATING) {
            Text(
                text = stringResource(R.string.detail_agent_working),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AgentMeta(agent: Agent) {
    val spacing = LocalSpacing.current
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.m),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(spacing.m),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            MetaRow(label = stringResource(R.string.detail_repo), value = agent.source.repository.displayRepository())
            MetaRow(label = stringResource(R.string.detail_branch), value = agent.displayBranch().ifBlank { "-" })
        }
    }
}

@Composable
private fun MetaRow(
    label: String,
    value: String,
) {
    val spacing = LocalSpacing.current
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(spacing.xxxl),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ConversationTurnCard(turn: ConversationTurn) {
    val spacing = LocalSpacing.current
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.m),
        color =
            if (turn.role == ConversationRole.User) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            },
        contentColor =
            if (turn.role == ConversationRole.User) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(spacing.m),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = turn.role.label,
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        if (turn.role == ConversationRole.User) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
                if (turn.relativeTimestamp.isNotBlank()) {
                    Text(
                        text = " · ${turn.relativeTimestamp}",
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            if (turn.role == ConversationRole.User) {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = EMPHASIS_MEDIUM)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                }
            }
            MarkdownText(text = turn.body)
        }
    }
}

@Composable
private fun MarkdownText(text: String) {
    val spacing = LocalSpacing.current
    val segments = rememberMarkdownSegments(text)
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        segments.forEach { segment ->
            if (segment.isCode) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(spacing.s)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.width(spacing.xs))
                            Text(
                                text = stringResource(R.string.detail_code),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.height(spacing.xs))
                        Text(
                            text = segment.content,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            } else if (segment.content.isNotBlank()) {
                Text(
                    text = segment.content,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun FollowUpBar(
    state: AgentDetailUiState.Content,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            if (state.sendError != null) {
                InlineError(text = state.sendError)
            }
            if (!state.inputEnabled) {
                Text(
                    text = state.agent.status.disabledInputMessage(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = spacing.m, end = spacing.m, top = spacing.s),
                )
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(spacing.m),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                PromptField(
                    value = state.followupText,
                    onValueChange = onValueChange,
                    placeholder = stringResource(R.string.detail_followup_hint),
                    minLines = 1,
                    maxLines = FOLLOWUP_MAX_LINES,
                    enabled = state.inputEnabled,
                    modifier = Modifier.weight(1f),
                )
                GhostIconButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    onClick = onSend,
                    contentDescription = stringResource(R.string.detail_send),
                    enabled = state.followupText.isNotBlank() && state.canSubmitFollowup,
                )
            }
        }
    }
}

@Composable
private fun InlineError(text: String) {
    val spacing = LocalSpacing.current
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(horizontal = spacing.m, vertical = spacing.xs),
    )
}

@Composable
private fun LoadingContent() {
    val spacing = LocalSpacing.current
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.m),
        verticalArrangement = Arrangement.spacedBy(spacing.s),
    ) {
        ShimmerBlock(modifier = Modifier.fillMaxWidth(FRACTION_HALF), height = spacing.m)
        ShimmerBlock(modifier = Modifier.fillMaxWidth(), height = spacing.m)
        ShimmerBlock(modifier = Modifier.fillMaxWidth(FRACTION_LARGE), height = spacing.xxl)
        ShimmerBlock(modifier = Modifier.fillMaxWidth(FRACTION_MEDIUM), height = spacing.m)
        ShimmerBlock(modifier = Modifier.fillMaxWidth(), height = spacing.xxl)
        ShimmerBlock(modifier = Modifier.fillMaxWidth(), height = spacing.xxl)
    }
}

private data class MarkdownSegment(
    val content: String,
    val isCode: Boolean,
)

@Composable
private fun rememberMarkdownSegments(text: String): List<MarkdownSegment> =
    remember(text) { parseMarkdownSegments(text) }

private fun parseMarkdownSegments(text: String): List<MarkdownSegment> {
    val segments = mutableListOf<MarkdownSegment>()
    val current = StringBuilder()
    var inCode = false
    text.lineSequence().forEach { line ->
        if (line.trimStart().startsWith(CODE_FENCE)) {
            if (current.isNotEmpty()) {
                segments += MarkdownSegment(current.toString().trim(), inCode)
                current.clear()
            }
            inCode = !inCode
        } else {
            current.appendLine(line)
        }
    }
    if (current.isNotEmpty()) {
        segments += MarkdownSegment(current.toString().trim(), inCode)
    }
    return segments.ifEmpty { listOf(MarkdownSegment(text, isCode = false)) }
}

private fun Agent.displayBranch(): String = target.branchName ?: branchName ?: source.ref.orEmpty()

private fun String.displayRepository(): String = removePrefix("https://github.com/").removeSuffix(".git")

private fun String.repositoryName(): String =
    displayRepository()
        .substringBefore("?")
        .substringBefore("#")
        .trimEnd('/')
        .substringAfterLast('/')
        .ifBlank { this }

@Composable
private fun AgentStatus.disabledInputMessage(): String =
    when (this) {
        AgentStatus.CREATING,
        AgentStatus.RUNNING,
        -> stringResource(R.string.detail_agent_working)
        AgentStatus.FINISHED -> ""
        AgentStatus.STOPPED -> stringResource(R.string.detail_followup_cancelled)
        AgentStatus.FAILED -> stringResource(R.string.detail_followup_failed)
    }

private fun fixtureContent(status: AgentStatus = AgentStatus.FINISHED) =
    AgentDetailUiState.Content(
        agent =
            Agent(
                id = "agt_123",
                status = status,
                source = Source(repository = "https://github.com/lawmight/cursor-agents-android", ref = "main"),
                target = Target(branchName = "cursor/agent-detail"),
                summary = "Build detail screen",
                createdAt = "2026-05-18T00:00:00Z",
            ),
        turns =
            listOf(
                ConversationTurn(
                    id = "m1",
                    role = ConversationRole.User,
                    body = "Build the detail screen.",
                    createdAt = Instant.parse("2026-05-18T00:00:00Z"),
                    relativeTimestamp = "2m ago",
                ),
                ConversationTurn(
                    id = "m2",
                    role = ConversationRole.Assistant,
                    body = "I'll inspect the navigation and repository APIs.\n\n```kotlin\nfun route(id: String) = \"agents/$id\"\n```",
                    createdAt = Instant.parse("2026-05-18T00:01:00Z"),
                    relativeTimestamp = "1m ago",
                ),
            ),
    )

@Preview(name = "Light - loaded", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightLoaded() {
    PreviewSurface(darkTheme = false) {
        AgentDetailContent(
            state = fixtureContent(),
            onBack = {},
            onFollowupChange = {},
            onSendFollowup = {},
            onRefresh = {},
        )
    }
}

@Preview(name = "Light - working", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightWorking() {
    PreviewSurface(darkTheme = false) {
        AgentDetailContent(
            state = fixtureContent(status = AgentStatus.RUNNING),
            onBack = {},
            onFollowupChange = {},
            onSendFollowup = {},
            onRefresh = {},
        )
    }
}

@Preview(name = "Light - loading", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightLoading() {
    PreviewSurface(darkTheme = false) {
        AgentDetailContent(
            state = AgentDetailUiState.Loading,
            onBack = {},
            onFollowupChange = {},
            onSendFollowup = {},
            onRefresh = {},
        )
    }
}

private const val CONVERSATION_HEADER_ITEMS = 4
private const val FOLLOWUP_MAX_LINES = 4
private const val CODE_FENCE = "```"
private const val EMPHASIS_MEDIUM = 0.72f
private const val FRACTION_HALF = 0.5f
private const val FRACTION_MEDIUM = 0.7f
private const val FRACTION_LARGE = 0.86f
