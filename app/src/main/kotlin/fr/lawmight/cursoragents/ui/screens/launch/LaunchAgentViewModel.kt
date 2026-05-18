package fr.lawmight.cursoragents.ui.screens.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.lawmight.cursoragents.api.CursorApiError
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.LaunchAgentRequest
import fr.lawmight.cursoragents.api.models.Prompt
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import fr.lawmight.cursoragents.di.CursorApiClientFactory
import fr.lawmight.cursoragents.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class LaunchAgentViewModel
    @Inject
    constructor(
        private val keyStore: EncryptedKeyStore,
        private val clientFactory: CursorApiClientFactory,
        private val agentsRepository: AgentsRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _formState = MutableStateFlow(LaunchAgentFormState())
        val formState: StateFlow<LaunchAgentFormState> = _formState.asStateFlow()

        fun onRepoChange(repo: String) {
            _formState.update { state ->
                state.copy(
                    repo = repo,
                    repoError = repo.validationError(),
                    launchResult = LaunchResult.Editing,
                )
            }
        }

        fun onBranchChange(branch: String) {
            _formState.update { state ->
                state.copy(branch = branch, launchResult = LaunchResult.Editing)
            }
        }

        fun onPromptChange(prompt: String) {
            _formState.update { state ->
                state.copy(
                    prompt = prompt,
                    promptError = prompt.promptValidationError(),
                    launchResult = LaunchResult.Editing,
                )
            }
        }

        fun onModelChange(model: ModelSelection) {
            _formState.update { state ->
                state.copy(model = model, launchResult = LaunchResult.Editing)
            }
        }

        fun submit() {
            val validated = formState.value.validated()
            _formState.value = validated
            if (!validated.canSubmit || validated.launchResult is LaunchResult.Submitting) return

            viewModelScope.launch {
                _formState.update { it.copy(launchResult = LaunchResult.Submitting) }
                val result =
                    runCatching {
                        withContext(ioDispatcher) {
                            createAgent(validated)
                        }
                    }
                result.fold(
                    onSuccess = { agent ->
                        withContext(ioDispatcher) {
                            agentsRepository.refresh()
                        }
                        _formState.update {
                            it.copy(launchResult = LaunchResult.Success(agent.id))
                        }
                    },
                    onFailure = { throwable ->
                        if (throwable is CancellationException) throw throwable
                        _formState.update {
                            it.copy(
                                launchResult =
                                    LaunchResult.Error(
                                        message = throwable.userMessage(),
                                        retryable = throwable.isRetryable(),
                                    ),
                            )
                        }
                    },
                )
            }
        }

        private suspend fun createAgent(state: LaunchAgentFormState): Agent {
            val apiKey = keyStore.get() ?: error("Add a Cursor API key before launching an agent.")
            val client = clientFactory.create(apiKey)
            return try {
                client
                    .createAgent(state.toRequest())
                    .getOrThrow()
            } finally {
                client.close()
            }
        }

        private fun LaunchAgentFormState.toRequest(): LaunchAgentRequest =
            LaunchAgentRequest(
                prompt = Prompt(text = prompt.trim()),
                source =
                    Source(
                        repository = normalizedRepositoryUrl(),
                        ref = branch.trim().ifBlank { null },
                    ),
                model = model.id,
            )

        private fun LaunchAgentFormState.validated(): LaunchAgentFormState =
            copy(
                repoError = repo.validationError(),
                promptError = prompt.promptValidationError(),
                launchResult = LaunchResult.Editing,
            )

        private fun LaunchAgentFormState.normalizedRepositoryUrl(): String = requireNotNull(repo.normalizedGitHubUrl())

        private fun String.validationError(): String? =
            when {
                isBlank() -> REPO_REQUIRED
                normalizedGitHubUrl() == null -> REPO_INVALID
                else -> null
            }

        private fun String.normalizedGitHubUrl(): String? {
            val value = trim().removeSuffix(".git").trimEnd('/')
            val match =
                GITHUB_URL_REGEX.matchEntire(value)
                    ?: SHORT_REPO_REGEX.matchEntire(value)
                    ?: return null
            val owner = match.groupValues[1]
            val repo = match.groupValues[2]
            return "https://github.com/$owner/$repo"
        }

        private fun String.promptValidationError(): String? =
            when {
                isBlank() -> PROMPT_REQUIRED
                trim().length < MIN_PROMPT_LENGTH -> PROMPT_TOO_SHORT
                else -> null
            }

        private fun String.userMessage(): String = ifBlank { "Could not launch the agent. Try again." }

        private fun Throwable.userMessage(): String = message?.userMessage() ?: "Could not launch the agent. Try again."

        private fun Throwable.isRetryable(): Boolean =
            when (this) {
                CursorApiError.Unauthorized,
                CursorApiError.Forbidden,
                CursorApiError.NotFound,
                is CursorApiError.DecodeError,
                -> false
                else -> true
            }

        private companion object {
            const val MIN_PROMPT_LENGTH = 10
            const val REPO_REQUIRED = "Enter a GitHub repository."
            const val REPO_INVALID = "Use owner/repo or https://github.com/owner/repo."
            const val PROMPT_REQUIRED = "Describe what the agent should do."
            const val PROMPT_TOO_SHORT = "Prompt must be at least 10 characters."

            val GITHUB_URL_REGEX =
                Regex("""https://github\.com/([A-Za-z0-9-]+)/([A-Za-z0-9._-]+)""")
            val SHORT_REPO_REGEX = Regex("""([A-Za-z0-9-]+)/([A-Za-z0-9._-]+)""")
        }
    }

data class LaunchAgentFormState(
    val repo: String = "",
    val branch: String = "",
    val prompt: String = "",
    val model: ModelSelection = ModelSelection.Default,
    val repoError: String? = null,
    val promptError: String? = null,
    val launchResult: LaunchResult = LaunchResult.Editing,
) {
    val canSubmit: Boolean
        get() =
            repo.isNotBlank() &&
                prompt.trim().length >= MIN_PROMPT_LENGTH &&
                repoError == null &&
                promptError == null &&
                launchResult !is LaunchResult.Submitting

    private companion object {
        const val MIN_PROMPT_LENGTH = 10
    }
}

sealed interface LaunchResult {
    data object Editing : LaunchResult

    data object Submitting : LaunchResult

    data class Success(val agentId: String) : LaunchResult

    data class Error(
        val message: String,
        val retryable: Boolean,
    ) : LaunchResult
}

enum class ModelSelection(
    val label: String,
    val id: String?,
) {
    Default("Default", null),
    ClaudeSonnet45("claude-sonnet-4.5", "claude-sonnet-4.5"),
    ClaudeSonnet45Thinking("claude-sonnet-4.5-thinking", "claude-sonnet-4.5-thinking"),
    Gpt5("gpt-5", "gpt-5"),
    Gpt5Codex("gpt-5-codex", "gpt-5-codex"),
}
