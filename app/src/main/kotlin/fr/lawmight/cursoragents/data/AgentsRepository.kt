package fr.lawmight.cursoragents.data

import fr.lawmight.cursoragents.data.api.Agent
import fr.lawmight.cursoragents.data.api.CursorApiClient
import fr.lawmight.cursoragents.data.api.FollowUpRequest
import fr.lawmight.cursoragents.data.api.LaunchAgentRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AgentsRepository private constructor(
    private val api: AgentsApi,
) {
    constructor(client: CursorApiClient) : this(CursorAgentsApi(client))

    internal constructor(
        api: AgentsApi,
        @Suppress("UNUSED_PARAMETER") marker: Unit,
    ) : this(api)

    private val _agents = MutableStateFlow<List<Agent>>(emptyList())
    val agents: StateFlow<List<Agent>> = _agents.asStateFlow()

    suspend fun refresh() {
        _agents.value = api.listAgents()
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun startPolling(
        intervalMs: Long = DEFAULT_POLLING_INTERVAL_MS,
        scope: CoroutineScope,
    ): Job =
        scope.launch {
            var backoffMs = FIRST_ERROR_BACKOFF_MS
            while (isActive) {
                try {
                    refresh()
                    backoffMs = FIRST_ERROR_BACKOFF_MS
                    delay(intervalMs)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    delay(backoffMs)
                    backoffMs = nextBackoff(backoffMs)
                }
            }
        }

    @Suppress("RedundantSuspendModifier")
    suspend fun observeAgent(id: String): Flow<Agent> {
        return agents.mapNotNull { agents -> agents.firstOrNull { it.id == id } }
    }

    suspend fun launch(req: LaunchAgentRequest): Result<Agent> =
        runCatching {
            val agent = api.launchAgent(req)
            _agents.update { current -> listOf(agent) + current.filterNot { it.id == agent.id } }
            agent
        }

    suspend fun stop(id: String) {
        api.stopAgent(id)
        refresh()
    }

    suspend fun delete(id: String) {
        api.deleteAgent(id)
        refresh()
    }

    suspend fun followUp(
        id: String,
        req: FollowUpRequest,
    ) {
        api.followUp(id, req)
        refresh()
    }

    private companion object {
        const val DEFAULT_POLLING_INTERVAL_MS = 5_000L
        const val FIRST_ERROR_BACKOFF_MS = 5_000L
        const val SECOND_ERROR_BACKOFF_MS = 10_000L
        const val MAX_ERROR_BACKOFF_MS = 30_000L

        fun nextBackoff(currentMs: Long): Long =
            when (currentMs) {
                FIRST_ERROR_BACKOFF_MS -> SECOND_ERROR_BACKOFF_MS
                else -> MAX_ERROR_BACKOFF_MS
            }
    }
}

internal interface AgentsApi {
    suspend fun listAgents(): List<Agent>

    suspend fun launchAgent(req: LaunchAgentRequest): Agent

    suspend fun stopAgent(id: String)

    suspend fun deleteAgent(id: String)

    suspend fun followUp(
        id: String,
        req: FollowUpRequest,
    )
}

private class CursorAgentsApi(
    private val client: CursorApiClient,
) : AgentsApi {
    override suspend fun listAgents(): List<Agent> = client.listAgents().agents

    override suspend fun launchAgent(req: LaunchAgentRequest): Agent = client.launchAgent(req)

    override suspend fun stopAgent(id: String) {
        client.stopAgent(id)
    }

    override suspend fun deleteAgent(id: String) {
        client.deleteAgent(id)
    }

    override suspend fun followUp(
        id: String,
        req: FollowUpRequest,
    ) {
        client.followUp(id, req)
    }
}
