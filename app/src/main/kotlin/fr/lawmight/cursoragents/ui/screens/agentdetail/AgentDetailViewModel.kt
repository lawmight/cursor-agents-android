package fr.lawmight.cursoragents.ui.screens.agentdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.ConversationMessage
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import fr.lawmight.cursoragents.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AgentDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val agentsRepository: AgentsRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : ViewModel() {
        private val agentId: String = checkNotNull(savedStateHandle[ARG_ID]) { "Missing agent id." }

        private val _uiState = MutableStateFlow<AgentDetailUiState>(AgentDetailUiState.Loading)
        val uiState: StateFlow<AgentDetailUiState> = _uiState.asStateFlow()

        private var latestAgent: Agent? = null
        private var latestTurns: List<ConversationTurn> = emptyList()
        private var hasLoadedConversation = false
        private var refreshJob: Job? = null

        init {
            observeAgent()
            refresh()
        }

        fun onFollowupChange(text: String) {
            _uiState.update { state ->
                if (state is AgentDetailUiState.Content) {
                    state.copy(followupText = text, sendError = null)
                } else {
                    state
                }
            }
        }

        fun refresh() {
            if (refreshJob?.isActive == true) return

            refreshJob =
                viewModelScope.launch {
                    setRefreshing(true)
                    runCatching {
                        withContext(ioDispatcher) {
                            val agent = agentsRepository.refreshAgent(agentId) ?: error(NO_API_KEY_MESSAGE)
                            val conversation = agentsRepository.conversation(agentId) ?: error(NO_API_KEY_MESSAGE)
                            agent to conversation
                        }
                    }.fold(
                        onSuccess = { (agent, messages) ->
                            latestAgent = agent
                            latestTurns = messages.toTurns()
                            hasLoadedConversation = true
                            emitContent(agent = agent, isRefreshing = false)
                        },
                        onFailure = { throwable ->
                            if (throwable is CancellationException) throw throwable
                            setRefreshing(false)
                            if (_uiState.value is AgentDetailUiState.Content) {
                                _uiState.update { state ->
                                    (state as AgentDetailUiState.Content).copy(
                                        refreshError = throwable.userMessage(),
                                    )
                                }
                            } else {
                                _uiState.value = AgentDetailUiState.Error(throwable.userMessage())
                            }
                        },
                    )
                }
        }

        fun sendFollowup(text: String) {
            val state = _uiState.value as? AgentDetailUiState.Content ?: return
            val prompt = text.trim()
            if (prompt.isBlank() || !state.canSubmitFollowup) return

            val optimisticTurn =
                ConversationTurn(
                    id = "optimistic-${UUID.randomUUID()}",
                    role = ConversationRole.User,
                    body = prompt,
                    createdAt = Instant.now(),
                    relativeTimestamp = "just now",
                    isOptimistic = true,
                )
            val previousInput = state.followupText
            latestTurns = state.turns + optimisticTurn
            _uiState.value =
                state.copy(
                    turns = latestTurns,
                    followupText = "",
                    sending = true,
                    sendError = null,
                    refreshError = null,
                )

            viewModelScope.launch {
                runCatching {
                    withContext(ioDispatcher) {
                        agentsRepository.followUp(agentId, prompt)
                        val agent = agentsRepository.refreshAgent(agentId) ?: latestAgent
                        val conversation = agentsRepository.conversation(agentId).orEmpty()
                        agent to conversation
                    }
                }.fold(
                    onSuccess = { (agent, messages) ->
                        agent?.let { latestAgent = it }
                        latestTurns = messages.toTurns().withOptimisticFallback(optimisticTurn)
                        emitContent(
                            agent = latestAgent,
                            followupText = "",
                            sending = false,
                            sendError = null,
                        )
                    },
                    onFailure = { throwable ->
                        if (throwable is CancellationException) throw throwable
                        latestTurns = latestTurns.filterNot { it.id == optimisticTurn.id }
                        emitContent(
                            agent = latestAgent,
                            followupText = previousInput,
                            sending = false,
                            sendError = throwable.userMessage(),
                        )
                    },
                )
            }
        }

        private fun observeAgent() {
            viewModelScope.launch {
                agentsRepository.agentFlow(agentId).collect { agent ->
                    latestAgent = agent
                    if (hasLoadedConversation) {
                        val current = _uiState.value as? AgentDetailUiState.Content
                        emitContent(
                            agent = agent,
                            followupText = current?.followupText.orEmpty(),
                            sending = current?.sending ?: false,
                            sendError = current?.sendError,
                            refreshError = current?.refreshError,
                            isRefreshing = current?.isRefreshing ?: false,
                        )
                    }
                }
            }
        }

        private fun setRefreshing(isRefreshing: Boolean) {
            _uiState.update { state ->
                if (state is AgentDetailUiState.Content) {
                    state.copy(isRefreshing = isRefreshing, refreshError = null)
                } else {
                    state
                }
            }
        }

        private fun emitContent(
            agent: Agent?,
            followupText: String = (_uiState.value as? AgentDetailUiState.Content)?.followupText.orEmpty(),
            sending: Boolean = (_uiState.value as? AgentDetailUiState.Content)?.sending ?: false,
            sendError: String? = (_uiState.value as? AgentDetailUiState.Content)?.sendError,
            refreshError: String? = (_uiState.value as? AgentDetailUiState.Content)?.refreshError,
            isRefreshing: Boolean = false,
        ) {
            val nextAgent = agent ?: return
            _uiState.value =
                AgentDetailUiState.Content(
                    agent = nextAgent,
                    turns = latestTurns,
                    followupText = followupText,
                    sending = sending,
                    isRefreshing = isRefreshing,
                    sendError = sendError,
                    refreshError = refreshError,
                )
        }

        private fun List<ConversationTurn>.withOptimisticFallback(
            optimisticTurn: ConversationTurn,
        ): List<ConversationTurn> =
            if (any { it.role == optimisticTurn.role && it.body == optimisticTurn.body }) {
                this
            } else {
                this + optimisticTurn
            }

        private fun List<ConversationMessage>.toTurns(): List<ConversationTurn> = map { it.toTurn() }

        private fun ConversationMessage.toTurn(): ConversationTurn =
            ConversationTurn(
                id = id,
                role = type.toConversationRole(),
                body = text.orEmpty(),
                createdAt = createdAt?.toInstantOrNull(),
                relativeTimestamp = createdAt?.relativeToNow().orEmpty(),
            )

        private fun String.toConversationRole(): ConversationRole =
            when (lowercase()) {
                "user" -> ConversationRole.User
                "tool" -> ConversationRole.Tool
                else -> ConversationRole.Assistant
            }

        private fun String.toInstantOrNull(): Instant? = runCatching { Instant.parse(this) }.getOrNull()

        private fun String.relativeToNow(): String =
            toInstantOrNull()
                ?.let { instant ->
                    val duration = Duration.between(instant, Instant.now())
                    when {
                        duration.isNegative || duration.seconds < SECONDS_PER_MINUTE -> "just now"
                        duration.toMinutes() < MINUTES_PER_HOUR -> "${duration.toMinutes()}m ago"
                        duration.toHours() < HOURS_PER_DAY -> "${duration.toHours()}h ago"
                        else -> "${duration.toDays()}d ago"
                    }
                }.orEmpty()

        private fun Throwable.userMessage(): String =
            message?.takeIf { it.isNotBlank() } ?: "Check your connection and try again."

        private companion object {
            const val ARG_ID = "id"
            const val NO_API_KEY_MESSAGE = "Add a Cursor API key before viewing this agent."
            const val SECONDS_PER_MINUTE = 60
            const val MINUTES_PER_HOUR = 60
            const val HOURS_PER_DAY = 24
        }
    }

sealed interface AgentDetailUiState {
    data object Loading : AgentDetailUiState

    data class Content(
        val agent: Agent,
        val turns: List<ConversationTurn>,
        val sending: Boolean = false,
        val followupText: String = "",
        val isRefreshing: Boolean = false,
        val sendError: String? = null,
        val refreshError: String? = null,
    ) : AgentDetailUiState {
        val canSubmitFollowup: Boolean
            get() = agent.status == AgentStatus.FINISHED && !sending

        val inputEnabled: Boolean
            get() = agent.status == AgentStatus.FINISHED && !sending
    }

    data class Error(val message: String) : AgentDetailUiState
}

data class ConversationTurn(
    val id: String,
    val role: ConversationRole,
    val body: String,
    val createdAt: Instant? = null,
    val relativeTimestamp: String = "",
    val isOptimistic: Boolean = false,
)

enum class ConversationRole(val label: String) {
    User("User"),
    Assistant("Assistant"),
    Tool("Tool"),
}
