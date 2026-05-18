package fr.lawmight.cursoragents.ui.screens.launch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.ui.components.GhostIconButton
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.PrimaryButton
import fr.lawmight.cursoragents.ui.components.PromptField
import fr.lawmight.cursoragents.ui.components.SectionHeader
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun LaunchAgentScreen(
    onBack: () -> Unit,
    onLaunched: (String) -> Unit,
    viewModel: LaunchAgentViewModel = hiltViewModel(),
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    val launchResult = state.launchResult
    LaunchedEffect(launchResult) {
        if (launchResult is LaunchResult.Success) {
            onLaunched(launchResult.agentId)
        }
    }

    LaunchAgentContent(
        state = state,
        onBack = onBack,
        onRepoChange = viewModel::onRepoChange,
        onBranchChange = viewModel::onBranchChange,
        onPromptChange = viewModel::onPromptChange,
        onModelChange = viewModel::onModelChange,
        onLaunch = viewModel::submit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchAgentContent(
    state: LaunchAgentFormState,
    onBack: () -> Unit,
    onRepoChange: (String) -> Unit,
    onBranchChange: (String) -> Unit,
    onPromptChange: (String) -> Unit,
    onModelChange: (ModelSelection) -> Unit,
    onLaunch: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val isSubmitting = state.launchResult is LaunchResult.Submitting
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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Box(modifier = Modifier.padding(spacing.m)) {
                        PrimaryButton(
                            text = stringResource(R.string.launch_submit),
                            onClick = onLaunch,
                            enabled = state.canSubmit,
                            loading = isSubmitting,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(pad),
            contentPadding = PaddingValues(bottom = spacing.l),
        ) {
            item { SectionHeader(text = stringResource(R.string.launch_repo)) }
            item {
                PromptField(
                    value = state.repo,
                    onValueChange = onRepoChange,
                    placeholder = stringResource(R.string.launch_repo_hint),
                    enabled = !isSubmitting,
                    isError = state.repoError != null,
                    errorMessage = state.repoError,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Uri,
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.m),
                )
            }

            item { Spacer(Modifier.height(spacing.m)) }
            item { SectionHeader(text = stringResource(R.string.launch_branch)) }
            item {
                PromptField(
                    value = state.branch,
                    onValueChange = onBranchChange,
                    placeholder = stringResource(R.string.launch_branch_hint),
                    enabled = !isSubmitting,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                        ),
                    modifier =
                        Modifier
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
                    enabled = !isSubmitting,
                    isError = state.promptError != null,
                    errorMessage = state.promptError,
                    minLines = 5,
                    maxLines = 8,
                    showCharCount = true,
                    maxChars = 4000,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.m),
                )
            }

            item { Spacer(Modifier.height(spacing.m)) }
            item { SectionHeader(text = stringResource(R.string.launch_model)) }
            item {
                ModelSelector(
                    selected = state.model,
                    enabled = !isSubmitting,
                    onModelChange = onModelChange,
                    modifier = Modifier.padding(horizontal = spacing.m),
                )
            }

            if (state.launchResult is LaunchResult.Error) {
                item { Spacer(Modifier.height(spacing.m)) }
                item {
                    Text(
                        text = state.launchResult.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = spacing.m),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModelSelector(
    selected: ModelSelection,
    enabled: Boolean,
    onModelChange: (ModelSelection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        ModelSelection.entries.forEach { model ->
            FilterChip(
                selected = selected == model,
                onClick = { onModelChange(model) },
                enabled = enabled,
                label = { Text(model.label) },
            )
        }
    }
}

@Preview(name = "Light - empty", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewLightEmpty() {
    PreviewSurface(darkTheme = false) {
        LaunchAgentContent(
            state = LaunchAgentFormState(),
            onBack = {},
            onRepoChange = {},
            onBranchChange = {},
            onPromptChange = {},
            onModelChange = {},
            onLaunch = {},
        )
    }
}

@Preview(name = "Light - filled", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewLightFilled() {
    PreviewSurface(darkTheme = false) {
        LaunchAgentContent(
            state =
                LaunchAgentFormState(
                    repo = "lawmight/cursor-agents-android",
                    prompt =
                        "Build the launch agent screen and wire the create flow into navigation.",
                    model = ModelSelection.ClaudeSonnet45,
                ),
            onBack = {},
            onRepoChange = {},
            onBranchChange = {},
            onPromptChange = {},
            onModelChange = {},
            onLaunch = {},
        )
    }
}

@Preview(name = "Dark - submitting", widthDp = 360, heightDp = 720)
@Composable
private fun LaunchPreviewDarkSubmitting() {
    PreviewSurface(darkTheme = true) {
        LaunchAgentContent(
            state =
                LaunchAgentFormState(
                    repo = "https://github.com/lawmight/cursor-agents-android",
                    branch = "main",
                    prompt = "Add detail screen follow-up support and verify state refreshes.",
                    launchResult = LaunchResult.Submitting,
                ),
            onBack = {},
            onRepoChange = {},
            onBranchChange = {},
            onPromptChange = {},
            onModelChange = {},
            onLaunch = {},
        )
    }
}
