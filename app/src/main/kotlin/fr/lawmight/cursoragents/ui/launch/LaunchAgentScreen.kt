package fr.lawmight.cursoragents.ui.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.ui.components.GhostIconButton
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.PrimaryButton
import fr.lawmight.cursoragents.ui.components.PromptField
import fr.lawmight.cursoragents.ui.components.RepoChip
import fr.lawmight.cursoragents.ui.components.SectionHeader
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

data class LaunchUiState(
    val repoOwner: String = "",
    val repoName: String = "",
    val branch: String = "main",
    val prompt: String = "",
    val autoCreatePr: Boolean = true,
    val isLaunching: Boolean = false,
    val error: String? = null,
) {
    val hasRepo: Boolean get() = repoOwner.isNotBlank() && repoName.isNotBlank()
    val canLaunch: Boolean get() = hasRepo && prompt.isNotBlank() && !isLaunching
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchAgentScreen(onLaunched: () -> Unit) {
    var state by remember { mutableStateOf(LaunchUiState()) }
    LaunchAgentContent(
        state = state,
        onBack = onLaunched,
        onPickRepo = {
            // Repository picker stub — wired in a CUR-N follow-up.
            state = state.copy(repoOwner = "lawmight", repoName = "cursor-agents-android")
        },
        onClearRepo = { state = state.copy(repoOwner = "", repoName = "") },
        onBranchChange = { state = state.copy(branch = it) },
        onPromptChange = { state = state.copy(prompt = it) },
        onAutoPrChange = { state = state.copy(autoCreatePr = it) },
        onLaunch = {
            if (!state.canLaunch) return@LaunchAgentContent
            state = state.copy(isLaunching = true)
            onLaunched()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchAgentContent(
    state: LaunchUiState,
    onBack: () -> Unit,
    onPickRepo: () -> Unit,
    onClearRepo: () -> Unit,
    onBranchChange: (String) -> Unit,
    onPromptChange: (String) -> Unit,
    onAutoPrChange: (Boolean) -> Unit,
    onLaunch: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.launch_title), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    GhostIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBack,
                        contentDescription = stringResource(R.string.detail_back),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Box(modifier = Modifier.padding(spacing.m)) {
                        PrimaryButton(
                            text = stringResource(R.string.launch_submit),
                            onClick = onLaunch,
                            enabled = state.canLaunch,
                            loading = state.isLaunching,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad),
            contentPadding = PaddingValues(bottom = spacing.l),
        ) {
            item { SectionHeader(text = stringResource(R.string.launch_repo)) }
            item {
                Box(modifier = Modifier.padding(horizontal = spacing.m)) {
                    if (state.hasRepo) {
                        RepoChip(
                            owner = state.repoOwner,
                            name = state.repoName,
                            onClick = onPickRepo,
                            onClose = onClearRepo,
                        )
                    } else {
                        EmptyRepoSlot(onPick = onPickRepo)
                    }
                }
            }

            item { Spacer(Modifier.height(spacing.m)) }
            item { SectionHeader(text = stringResource(R.string.launch_branch)) }
            item {
                PromptField(
                    value = state.branch,
                    onValueChange = onBranchChange,
                    placeholder = stringResource(R.string.launch_branch_hint),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.m),
                )
            }

            item { Spacer(Modifier.height(spacing.m)) }
            item { SectionHeader(text = stringResource(R.string.launch_prompt)) }
            item {
                PromptField(
                    value = state.prompt,
                    onValueChange = onPromptChange,
                    placeholder = stringResource(R.string.launch_prompt_hint),
                    minLines = 5,
                    maxLines = 12,
                    showCharCount = true,
                    maxChars = 4000,
                    trailingIcon = {
                        Row {
                            GhostIconButton(
                                Icons.Default.PhotoLibrary,
                                onClick = {},
                                contentDescription = stringResource(R.string.launch_attach_image),
                            )
                            GhostIconButton(
                                Icons.Default.Mic,
                                onClick = {},
                                contentDescription = stringResource(R.string.launch_voice),
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.m),
                )
            }

            item { Spacer(Modifier.height(spacing.m)) }
            item { SectionHeader(text = stringResource(R.string.launch_options)) }
            item {
                AutoPrRow(
                    enabled = state.autoCreatePr,
                    onChange = onAutoPrChange,
                    modifier = Modifier.padding(horizontal = spacing.m),
                )
            }

            if (state.error != null) {
                item { Spacer(Modifier.height(spacing.m)) }
                item {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = spacing.m),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyRepoSlot(onPick: () -> Unit) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onPick)
            .padding(horizontal = spacing.m, vertical = spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.launch_repo_pick),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AutoPrRow(
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onChange(!enabled) }
            .padding(horizontal = spacing.m, vertical = spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.launch_auto_create_pr),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.launch_auto_create_pr_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - empty", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewLightEmpty() {
    PreviewSurface(darkTheme = false) {
        LaunchAgentContent(
            state = LaunchUiState(),
            onBack = {}, onPickRepo = {}, onClearRepo = {},
            onBranchChange = {}, onPromptChange = {}, onAutoPrChange = {}, onLaunch = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light - filled", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewLightFilled() {
    PreviewSurface(darkTheme = false) {
        LaunchAgentContent(
            state = LaunchUiState(
                repoOwner = "lawmight",
                repoName = "cursor-agents-android",
                branch = "main",
                prompt = "Refactor the navigation host to use type-safe routes and remove the legacy agent/{id} pattern.",
                autoCreatePr = true,
            ),
            onBack = {}, onPickRepo = {}, onClearRepo = {},
            onBranchChange = {}, onPromptChange = {}, onAutoPrChange = {}, onLaunch = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Dark - empty", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewDarkEmpty() {
    PreviewSurface(darkTheme = true) {
        LaunchAgentContent(
            state = LaunchUiState(),
            onBack = {}, onPickRepo = {}, onClearRepo = {},
            onBranchChange = {}, onPromptChange = {}, onAutoPrChange = {}, onLaunch = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Dark - filled launching", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewDarkLaunching() {
    PreviewSurface(darkTheme = true) {
        LaunchAgentContent(
            state = LaunchUiState(
                repoOwner = "lawmight",
                repoName = "cursor-agents-android",
                branch = "main",
                prompt = "Bundle Inter + JetBrains Mono fonts and rewrite Type.kt.",
                isLaunching = true,
            ),
            onBack = {}, onPickRepo = {}, onClearRepo = {},
            onBranchChange = {}, onPromptChange = {}, onAutoPrChange = {}, onLaunch = {},
        )
    }
}
