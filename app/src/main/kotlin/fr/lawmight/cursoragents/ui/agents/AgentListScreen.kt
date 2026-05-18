package fr.lawmight.cursoragents.ui.agents

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

sealed interface AgentListUiState {
    data object Loading : AgentListUiState

    data class Empty(val isRefreshing: Boolean = false) : AgentListUiState

    data class Loaded(
        // Agent plus cached "age" string.
        val agents: List<Pair<Agent, String>>,
        val isRefreshing: Boolean = false,
    ) : AgentListUiState

    data class Error(val message: String) : AgentListUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentListScreen(
    onLaunch: () -> Unit,
    onSettings: () -> Unit,
    onOpen: (String) -> Unit,
) {
    // ViewModel wiring follows in a CUR-N fast-follow. For now drive a static
    // fixture so the screen renders end-to-end on a device.
    val state = remember { fixtureLoaded() }
    AgentListContent(
        state = state,
        onLaunch = onLaunch,
        onSettings = onSettings,
        onOpen = onOpen,
        onRefresh = {},
        onRetry = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentListContent(
    state: AgentListUiState,
    onLaunch: () -> Unit,
    onSettings: () -> Unit,
    onOpen: (String) -> Unit,
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
                onClick = onLaunch,
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
                is AgentListUiState.Loaded -> state.isRefreshing
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
                        actionLabel = stringResource(R.string.agents_new),
                        onAction = onLaunch,
                    )
                is AgentListUiState.Loaded ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                start = spacing.m,
                                end = spacing.m,
                                top = spacing.s,
                                // Make room for the FAB.
                                bottom = 96.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(spacing.s),
                    ) {
                        items(items = state.agents, key = { it.first.id }) { (agent, age) ->
                            AgentCard(agent = agent, age = age, onClick = { onOpen(agent.id) })
                        }
                    }
                is AgentListUiState.Error ->
                    ErrorState(
                        title = stringResource(R.string.agents_error_title),
                        body = state.message,
                        retryLabel = stringResource(R.string.agents_retry),
                        onRetry = onRetry,
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

private fun fixtureLoaded() =
    AgentListUiState.Loaded(
        agents =
            listOf(
                fixtureAgent("a1", AgentStatus.RUNNING, "Refactor navigation host to use type-safe routes") to "2m ago",
                fixtureAgent("a2", AgentStatus.FINISHED, "PR #42: Visual foundation v0") to "1h ago",
                fixtureAgent("a3", AgentStatus.CREATING, "Bundle Inter + JetBrains Mono fonts") to "5m ago",
                fixtureAgent("a4", AgentStatus.FAILED, "Failed: detekt complexity > 15") to "yesterday",
                fixtureAgent("a5", AgentStatus.STOPPED, "Stopped by user") to "2d ago",
            ),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - loaded", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightLoaded() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = fixtureLoaded(),
            onLaunch = {},
            onSettings = {},
            onOpen = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - empty", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightEmpty() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = AgentListUiState.Empty(),
            onLaunch = {},
            onSettings = {},
            onOpen = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - loading", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightLoading() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = AgentListUiState.Loading,
            onLaunch = {},
            onSettings = {},
            onOpen = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - error", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewLightError() {
    PreviewSurface(darkTheme = false) {
        AgentListContent(
            state = AgentListUiState.Error("Check your connection and try again. (HTTP 503)"),
            onLaunch = {},
            onSettings = {},
            onOpen = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Dark - loaded", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewDarkLoaded() {
    PreviewSurface(darkTheme = true) {
        AgentListContent(
            state = fixtureLoaded(),
            onLaunch = {},
            onSettings = {},
            onOpen = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Dark - empty", widthDp = 360, heightDp = 720)
@Composable
private fun AgentListPreviewDarkEmpty() {
    PreviewSurface(darkTheme = true) {
        AgentListContent(
            state = AgentListUiState.Empty(),
            onLaunch = {},
            onSettings = {},
            onOpen = {},
            onRefresh = {},
            onRetry = {},
        )
    }
}
