package fr.lawmight.cursoragents.data.repository

import fr.lawmight.cursoragents.api.CursorApiClient
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.ConversationMessage
import fr.lawmight.cursoragents.api.models.FollowUpRequest
import fr.lawmight.cursoragents.api.models.LaunchAgentRequest
import fr.lawmight.cursoragents.api.models.Prompt
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.di.CursorApiClientFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AgentsRepository(
    private val keyStore: EncryptedKeyStore,
    private val clientFactory: CursorApiClientFactory,
    private val scope: CoroutineScope,
    private val pollingIntervalMillis: Long = DEFAULT_POLLING_INTERVAL_MILLIS,
) {
    private val _agents = MutableStateFlow<List<Agent>>(emptyList())
    private val refreshMutex = Mutex()

    val agentsFlow: Flow<List<Agent>> = _agents

    private val pollingJob: Job =
        scope.launch {
            while (isActive) {
                runCatching { refresh() }
                delay(pollingIntervalMillis)
            }
        }

    fun agentFlow(id: String): Flow<Agent> =
        agentsFlow
            .mapNotNull { agents -> agents.firstOrNull { it.id == id } }
            .distinctUntilChanged()

    suspend fun refresh() {
        withClient { client ->
            refreshMutex.withLock {
                _agents.value = client.fetchAllAgents()
            }
        }
    }

    suspend fun refreshAgent(id: String): Agent? =
        withClient { client ->
            client.getAgent(id).getOrThrow().also { agent ->
                _agents.value = listOf(agent) + _agents.value.filterNot { it.id == agent.id }
            }
        }

    suspend fun launch(req: LaunchAgentRequest): Agent? =
        withClient { client ->
            client.createAgent(req).getOrThrow().also { agent ->
                _agents.value = listOf(agent) + _agents.value.filterNot { it.id == agent.id }
            }
        }

    suspend fun conversation(id: String): List<ConversationMessage>? =
        withClient { client ->
            client.getAgentConversation(id).getOrThrow()
        }

    suspend fun followUp(
        id: String,
        text: String,
    ) {
        withClient { client ->
            client.addFollowUp(id, FollowUpRequest(prompt = Prompt(text))).getOrThrow()
        }
    }

    suspend fun stop(id: String) {
        withClient { client ->
            client.stopAgent(id).getOrThrow()
            refresh()
        }
    }

    suspend fun delete(id: String) {
        withClient { client ->
            client.deleteAgent(id).getOrThrow()
            _agents.value = _agents.value.filterNot { it.id == id }
        }
    }

    fun close() {
        pollingJob.cancel()
    }

    private suspend fun CursorApiClient.fetchAllAgents(): List<Agent> {
        val agents = mutableListOf<Agent>()
        var cursor: String? = null
        do {
            val page = listAgents(cursor = cursor).getOrThrow()
            agents += page.agents
            cursor = page.nextCursor
        } while (cursor != null)
        return agents
    }

    private suspend fun <T> withClient(block: suspend (CursorApiClient) -> T): T? {
        val apiKey = keyStore.get() ?: return null
        val client = clientFactory.create(apiKey)
        return try {
            block(client)
        } finally {
            client.close()
        }
    }

    companion object {
        const val DEFAULT_POLLING_INTERVAL_MILLIS = 15_000L
    }
}
