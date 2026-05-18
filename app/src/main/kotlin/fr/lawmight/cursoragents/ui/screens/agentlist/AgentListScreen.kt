package fr.lawmight.cursoragents.ui.screens.agentlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.api.models.Target
import fr.lawmight.cursoragents.ui.brand.BrandWordmark
import fr.lawmight.cursoragents.ui.components.AgentCard
import fr.lawmight.cursoragents.ui.components.AgentCardShimmer
import fr.lawmight.cursoragents.ui.components.EmptyState
import fr.lawmight.cursoragents.ui.components.ErrorState
import fr.lawmight.cursoragents.ui.components.GhostIconButton
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun AgentListScreen(
    onCreateAgent: () -> Unit,
    onSettings: () -> Unit,
    onOpenAgent: (String) -> Unit,
    viewModel: AgentListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AgentListContent(
        state = state,
        onCreateAgent = onCreateAgent,
        onSettings = onSettings,
        onOpenAgent = onOpenAgent,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentListContent(
    state: AgentListUiState,
    onCreateAgent: () -> Unit,
    onSettings: () -> Unit,
    onOpenAgent: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { BrandWordmark() },
                actions = {
                    GhostIconButton(
                        icon = Icons.Default.Settings,
                        onClick = onSettings,
                        contentDescription = stringResource(R.string.settings_title),
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateAgent,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.agents_new)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        val isRefreshing =
            when (state) {
                is AgentListUiState.Content -> state.isRefreshing
                is AgentListUiState.Empty -> state.isRefreshing
                else -> false
            }
        val pullState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(pad),
        ) {
            when (state) {
                is AgentListUiState.Loading -> LoadingContent()
                is AgentListUiState.Empty ->
                    EmptyState(
                        title = stringResource(R.string.agents_empty_title),
                        body = stringResource(R.string.agents_empty_body),
                        actionLabel = stringResource(R.string.agents_create_first),
                        onAction = onCreateAgent,
                    )
                is AgentListUiState.Content ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = spacing.m,
                                end = spacing.m,
                                top = spacing.s,
                                bottom = spacing.xxxl,
                            ),
                        verticalArrangement = Arrangement.spacedBy(spacing.s),
                    ) {
                        items(items = state.agents, key = { it.agent.id }) { item ->
                            AgentCard(
                                agent = item.agent,
                                idLabel = item.idLabel,
                                repositoryName = item.repositoryName,
                                age = item.createdAtRelative,
                                branchName = item.branchName,
                                onClick = { onOpenAgent(item.agent.id) },
                            )
                        }
                    }
                is AgentListUiState.Error ->
                    ErrorState(
                        title = stringResource(R.string.agents_error_title),
                        body = state.message,
                        retryLabel = stringResource(R.string.agents_retry),
                        onRetry = if (state.canRetry) onRetry else null,
                    )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    val spacing = LocalSpacing.current
    LazyColumn(
        contentPadding = PaddingValues(spacing.m),
        verticalArrangement = Arrangement.spacedBy(spacing.s),
    ) {
        items(count = 5) { AgentCardShimmer() }
    }
}

private fun fixtureAgent(
    id: String,
    status: AgentStatus,
    summary: String?,
    branch: String? = "main",
): Agent =
    Agent(
        id = id,
        status = status,
        source = Source(repository = "https://github.com/lawmight/cursor-agents-android", ref = branch),
        target = Target(branchName = "cursor/$id"),
        summary = summary,
        createdAt = "2026-05-12T08:00:00Z",
    )

private fun fixtureContent() =
    AgentListUiState.Content(
        agents =
            listOf(
                AgentListItem(
                    agent = fixtureAgent("agt_1234567890", AgentStatus.RUNNING, "Refactor navigation host"),
                    idLabel = "agt_1234...",
                    repositoryName = "cursor-agents-android",
                    createdAtRelative = "2m ago",
                    branchName = "cursor/agt_1234567890",
                ),
                AgentListItem(
                    agent = fixtureAgent("agt_finished", AgentStatus.FINISHED, "PR #42: Visual foundation"),
                    idLabel = "agt_fini...",
                    repositoryName = "cursor-agents-android",
                    createdAtRelative = "1h ago",
                    branchName = "cursor/agt_finished",
                ),
                AgentListItem(
                    agent = fixtureAgent("agt_failed", AgentStatus.FAILED, "Failed: detekt complexity > 15"),
                    idLabel = "agt_fail...",
                    repositoryName = "cursor-agents-android",
                    createdAtRelative = "2d ago",
                    branchName = "cursor/agt_failed",
                ),
            ),
    )

@Preview(name = "Light - loaded", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightLoaded() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = fixtureContent(),
            onCreateAgent = {},
            onSettings = {},
            onOpenAgent = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview(name = "Light - empty", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightEmpty() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = AgentListUiState.Empty(),
            onCreateAgent = {},
            onSettings = {},
            onOpenAgent = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview(name = "Light - loading", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightLoading() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = AgentListUiState.Loading,
            onCreateAgent = {},
            onSettings = {},
            onOpenAgent = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@Preview(name = "Light - error", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightError() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = AgentListUiState.Error("Check your connection and try again.", canRetry = true),
            onCreateAgent = {},
            onSettings = {},
            onOpenAgent = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}
