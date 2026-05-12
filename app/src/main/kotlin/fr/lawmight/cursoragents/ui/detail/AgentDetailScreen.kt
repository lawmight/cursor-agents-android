package fr.lawmight.cursoragents.ui.detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.data.api.Agent
import fr.lawmight.cursoragents.data.api.AgentConversation
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.data.api.ConversationMessage
import fr.lawmight.cursoragents.data.api.Source
import fr.lawmight.cursoragents.data.api.Target
import fr.lawmight.cursoragents.ui.components.EmptyState
import fr.lawmight.cursoragents.ui.components.ErrorState
import fr.lawmight.cursoragents.ui.components.GhostIconButton
import fr.lawmight.cursoragents.ui.components.MessageAuthor
import fr.lawmight.cursoragents.ui.components.MessageBubble
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.PromptField
import fr.lawmight.cursoragents.ui.components.SectionHeader
import fr.lawmight.cursoragents.ui.components.ShimmerBlock
import fr.lawmight.cursoragents.ui.components.StatusBadge

sealed interface AgentDetailUiState {
    data object Loading : AgentDetailUiState
    data class Loaded(
        val agent: Agent,
        val conversation: AgentConversation,
        val isSendingFollowup: Boolean = false,
    ) : AgentDetailUiState
    data class Error(val message: String) : AgentDetailUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDetailScreen(agentId: String, onClose: () -> Unit) {
    val state: AgentDetailUiState = remember(agentId) { fixtureLoaded(agentId) }
    var followup by remember { mutableStateOf("") }
    AgentDetailContent(
        state = state,
        followup = followup,
        onFollowupChange = { followup = it },
        onSendFollowup = { followup = "" },
        onClose = onClose,
        onRetry = {},
        onStop = {},
        onDelete = {},
        onOpenPr = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentDetailContent(
    state: AgentDetailUiState,
    followup: String,
    onFollowupChange: (String) -> Unit,
    onSendFollowup: () -> Unit,
    onClose: () -> Unit,
    onRetry: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    onOpenPr: () -> Unit,
) {
    val spacing = fr.lawmight.cursoragents.ui.theme.LocalSpacing.current
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.titleText(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (state is AgentDetailUiState.Loaded) {
                            Spacer(Modifier.size(spacing.xs))
                            StatusBadge(status = state.agent.status)
                        }
                    }
                },
                navigationIcon = {
                    GhostIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onClose,
                        contentDescription = stringResource(R.string.detail_back),
                    )
                },
                actions = {
                    if (state is AgentDetailUiState.Loaded) {
                        Box {
                            GhostIconButton(
                                icon = Icons.Default.MoreVert,
                                onClick = { menuOpen = true },
                                contentDescription = stringResource(R.string.detail_more),
                            )
                            DropdownMenu(
                                expanded = menuOpen,
                                onDismissRequest = { menuOpen = false },
                            ) {
                                if (state.agent.status == AgentStatus.RUNNING ||
                                    state.agent.status == AgentStatus.CREATING
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.detail_stop)) },
                                        leadingIcon = { Icon(Icons.Default.Stop, null) },
                                        onClick = { menuOpen = false; onStop() },
                                    )
                                }
                                if (state.agent.target.prUrl != null) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.detail_open_pr)) },
                                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) },
                                        onClick = { menuOpen = false; onOpenPr() },
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.detail_delete)) },
                                    leadingIcon = { Icon(Icons.Default.Delete, null) },
                                    onClick = { menuOpen = false; onDelete() },
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            if (state is AgentDetailUiState.Loaded) {
                FollowUpBar(
                    value = followup,
                    onValueChange = onFollowupChange,
                    onSend = onSendFollowup,
                    isSending = state.isSendingFollowup,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        when (state) {
            is AgentDetailUiState.Loading -> LoadingContent(modifier = Modifier.padding(pad))
            is AgentDetailUiState.Error -> ErrorState(
                title = stringResource(R.string.detail_error_title),
                body = state.message,
                onRetry = onRetry,
                modifier = Modifier.padding(pad),
            )
            is AgentDetailUiState.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(pad),
            )
        }
    }
}

@Composable
private fun LoadedContent(state: AgentDetailUiState.Loaded, modifier: Modifier = Modifier) {
    val spacing = fr.lawmight.cursoragents.ui.theme.LocalSpacing.current
    val listState = rememberLazyListState()
    LaunchedEffect(state.conversation.messages.size) {
        if (state.conversation.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.conversation.messages.size + 2)
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = spacing.l),
        verticalArrangement = Arrangement.spacedBy(spacing.s),
    ) {
        item { SectionHeader(text = stringResource(R.string.detail_repo)) }
        item {
            Row(
                modifier = Modifier.padding(horizontal = spacing.m),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.size(spacing.xs))
                Text(
                    text = state.agent.source.repository.removePrefix("https://github.com/") +
                        (state.agent.source.ref?.let { "  ·  $it" } ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        if (!state.agent.summary.isNullOrBlank()) {
            item { Spacer(Modifier.height(spacing.s)) }
            item { SectionHeader(text = stringResource(R.string.detail_summary)) }
            item {
                Text(
                    text = state.agent.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = spacing.m),
                )
            }
        }
        item { Spacer(Modifier.height(spacing.m)) }
        if (state.conversation.messages.isEmpty()) {
            item {
                EmptyState(
                    title = stringResource(R.string.detail_no_messages_title),
                    body = stringResource(R.string.detail_no_messages_body),
                )
            }
        } else {
            items(items = state.conversation.messages, key = { it.id }) { message ->
                Box(modifier = Modifier.padding(horizontal = spacing.m)) {
                    MessageBubble(
                        text = message.text,
                        author = if (message.type == "user") MessageAuthor.User else MessageAuthor.Agent,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    val spacing = fr.lawmight.cursoragents.ui.theme.LocalSpacing.current
    Column(modifier = modifier.padding(spacing.m)) {
        ShimmerBlock(modifier = Modifier.fillMaxWidth(0.5f), height = 18.dp)
        Spacer(Modifier.height(spacing.s))
        ShimmerBlock(modifier = Modifier.fillMaxWidth(), height = 14.dp)
        Spacer(Modifier.height(spacing.xs))
        ShimmerBlock(modifier = Modifier.fillMaxWidth(0.7f), height = 14.dp)
        Spacer(Modifier.height(spacing.l))
        repeat(3) {
            ShimmerBlock(modifier = Modifier.fillMaxWidth(0.4f), height = 12.dp)
            Spacer(Modifier.height(spacing.s))
            ShimmerBlock(modifier = Modifier.fillMaxWidth(0.85f), height = 60.dp)
            Spacer(Modifier.height(spacing.m))
        }
    }
}

@Composable
private fun FollowUpBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean,
) {
    val spacing = fr.lawmight.cursoragents.ui.theme.LocalSpacing.current
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(spacing.m),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                PromptField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = stringResource(R.string.detail_followup_hint),
                    minLines = 1,
                    maxLines = 4,
                    enabled = !isSending,
                    modifier = Modifier.weight(1f),
                )
                GhostIconButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    onClick = onSend,
                    contentDescription = stringResource(R.string.detail_send),
                    enabled = value.isNotBlank() && !isSending,
                )
            }
        }
    }
}

@Composable
private fun AgentDetailUiState.titleText(): String = when (this) {
    is AgentDetailUiState.Loading -> stringResource(R.string.detail_loading_title)
    is AgentDetailUiState.Error -> stringResource(R.string.detail_error_title)
    is AgentDetailUiState.Loaded -> agent.summary?.takeIf { it.isNotBlank() } ?: "Agent"
}

private fun fixtureLoaded(id: String) = AgentDetailUiState.Loaded(
    agent = Agent(
        id = id,
        status = AgentStatus.RUNNING,
        source = Source(repository = "https://github.com/lawmight/cursor-agents-android", ref = "main"),
        target = Target(branchName = "cursor/$id", prUrl = "https://github.com/lawmight/cursor-agents-android/pull/42"),
        summary = "Refactor navigation host",
        createdAt = "2026-05-12T08:00:00Z",
    ),
    conversation = AgentConversation(
        id = id,
        messages = listOf(
            ConversationMessage("m1", "user", "Refactor the navigation host to use type-safe routes."),
            ConversationMessage("m2", "agent", "I'll start by inspecting AppNavHost.kt and listing the current routes."),
            ConversationMessage("m3", "user", "Sounds good — also drop the legacy `agent/{id}` pattern."),
            ConversationMessage("m4", "agent", "Got it. I'll wire the new sealed Routes type and migrate the call sites."),
        ),
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - loaded", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightLoaded() {
    PreviewSurface(darkTheme = false) {
        AgentDetailContent(
            state = fixtureLoaded("a1"),
            followup = "",
            onFollowupChange = {},
            onSendFollowup = {},
            onClose = {}, onRetry = {}, onStop = {}, onDelete = {}, onOpenPr = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - empty messages", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightEmpty() {
    PreviewSurface(darkTheme = false) {
        val base = fixtureLoaded("a1")
        AgentDetailContent(
            state = base.copy(conversation = base.conversation.copy(messages = emptyList())),
            followup = "",
            onFollowupChange = {}, onSendFollowup = {},
            onClose = {}, onRetry = {}, onStop = {}, onDelete = {}, onOpenPr = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - loading", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightLoading() {
    PreviewSurface(darkTheme = false) {
        AgentDetailContent(
            state = AgentDetailUiState.Loading,
            followup = "",
            onFollowupChange = {}, onSendFollowup = {},
            onClose = {}, onRetry = {}, onStop = {}, onDelete = {}, onOpenPr = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - error", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewLightError() {
    PreviewSurface(darkTheme = false) {
        AgentDetailContent(
            state = AgentDetailUiState.Error("The agent may have been deleted, or your connection dropped."),
            followup = "",
            onFollowupChange = {}, onSendFollowup = {},
            onClose = {}, onRetry = {}, onStop = {}, onDelete = {}, onOpenPr = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Dark - loaded", widthDp = 360, heightDp = 720)
@Composable
private fun AgentDetailPreviewDarkLoaded() {
    PreviewSurface(darkTheme = true) {
        AgentDetailContent(
            state = fixtureLoaded("a1"),
            followup = "",
            onFollowupChange = {}, onSendFollowup = {},
            onClose = {}, onRetry = {}, onStop = {}, onDelete = {}, onOpenPr = {},
        )
    }
}
