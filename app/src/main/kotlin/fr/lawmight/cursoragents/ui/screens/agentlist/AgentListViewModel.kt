package fr.lawmight.cursoragents.ui.screens.agentlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import fr.lawmight.cursoragents.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AgentListViewModel
    @Inject
    constructor(
        private val repository: AgentsRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<AgentListUiState>(AgentListUiState.Loading)
        val uiState: StateFlow<AgentListUiState> = _uiState.asStateFlow()

        private var latestAgents: List<Agent> = emptyList()
        private var initialRefreshComplete = false
        private var isRefreshing = false
        private var refreshJob: Job? = null

        init {
            observeAgents()
            refresh()
        }

        fun refresh() {
            if (refreshJob?.isActive == true) return

            refreshJob =
                viewModelScope.launch {
                    isRefreshing = initialRefreshComplete
                    emitStateFromCache()
                    runCatching {
                        withContext(ioDispatcher) {
                            repository.refresh()
                        }
                    }.fold(
                        onSuccess = {
                            initialRefreshComplete = true
                            isRefreshing = false
                            emitStateFromCache()
                        },
                        onFailure = { throwable ->
                            if (throwable is CancellationException) throw throwable
                            initialRefreshComplete = true
                            isRefreshing = false
                            _uiState.value =
                                AgentListUiState.Error(
                                    message = throwable.userMessage(),
                                    canRetry = true,
                                )
                        },
                    )
                }
        }

        fun retry() {
            refresh()
        }

        private fun observeAgents() {
            viewModelScope.launch {
                repository.agentsFlow.collect { agents ->
                    latestAgents = agents
                    if (initialRefreshComplete || agents.isNotEmpty()) {
                        emitStateFromCache()
                    }
                }
            }
        }

        private fun emitStateFromCache() {
            _uiState.value =
                when {
                    !initialRefreshComplete && latestAgents.isEmpty() -> AgentListUiState.Loading
                    latestAgents.isEmpty() -> AgentListUiState.Empty(isRefreshing = isRefreshing)
                    else ->
                        AgentListUiState.Content(
                            agents = latestAgents.map { it.toListItem() },
                            isRefreshing = isRefreshing,
                        )
                }
        }

        private fun Agent.toListItem(): AgentListItem =
            AgentListItem(
                agent = this,
                idLabel = id.truncatedId(),
                repositoryName = source.repository.repositoryName(),
                createdAtRelative = createdAt.relativeToNow(),
                branchName = target.branchName ?: branchName ?: source.ref,
            )

        private fun String.truncatedId(): String =
            if (length <= AGENT_ID_PREFIX_LENGTH) {
                this
            } else {
                "${take(AGENT_ID_PREFIX_LENGTH)}..."
            }

        private fun String.repositoryName(): String {
            val normalized =
                removeSuffix(".git")
                    .substringBefore("?")
                    .substringBefore("#")
                    .substringAfterLast(":")
                    .trimEnd('/')
            return normalized.substringAfterLast('/').ifBlank { this }
        }

        private fun String.relativeToNow(): String =
            runCatching {
                val duration = Duration.between(Instant.parse(this), Instant.now())
                when {
                    duration.isNegative || duration.seconds < SECONDS_PER_MINUTE -> "just now"
                    duration.toMinutes() < MINUTES_PER_HOUR -> "${duration.toMinutes()}m ago"
                    duration.toHours() < HOURS_PER_DAY -> "${duration.toHours()}h ago"
                    else -> "${duration.toDays()}d ago"
                }
            }.getOrDefault("just now")

        private fun Throwable.userMessage(): String = message?.takeIf { it.isNotBlank() } ?: "Check your connection and try again."

        private companion object {
            const val AGENT_ID_PREFIX_LENGTH = 8
            const val SECONDS_PER_MINUTE = 60
            const val MINUTES_PER_HOUR = 60
            const val HOURS_PER_DAY = 24
        }
    }

sealed interface AgentListUiState {
    data object Loading : AgentListUiState

    data class Empty(val isRefreshing: Boolean = false) : AgentListUiState

    data class Content(
        val agents: List<AgentListItem>,
        val isRefreshing: Boolean = false,
    ) : AgentListUiState

    data class Error(
        val message: String,
        val canRetry: Boolean,
    ) : AgentListUiState
}

data class AgentListItem(
    val agent: Agent,
    val idLabel: String,
    val repositoryName: String,
    val createdAtRelative: String,
    val branchName: String?,
)
