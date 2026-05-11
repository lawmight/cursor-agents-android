package fr.lawmight.cursoragents.data.repository

import fr.lawmight.cursoragents.data.api.Agent
import fr.lawmight.cursoragents.data.api.AgentConversation
import fr.lawmight.cursoragents.data.api.CursorApiClient
import fr.lawmight.cursoragents.data.api.FollowUpRequest
import fr.lawmight.cursoragents.data.api.LaunchAgentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgentsRepository(
    private val clientProvider: (alias: String?) -> CursorApiClient?,
) {
    private val _agents = MutableStateFlow<List<Agent>>(emptyList())
    val agents: Flow<List<Agent>> = _agents.asStateFlow()

    suspend fun refresh(alias: String? = null) {
        val c = clientProvider(alias) ?: return
        runCatching { _agents.value = c.listAgents().agents }
    }

    suspend fun launch(req: LaunchAgentRequest, alias: String? = null): Agent? =
        clientProvider(alias)?.launchAgent(req)

    suspend fun conversation(id: String, alias: String? = null): AgentConversation? =
        clientProvider(alias)?.getConversation(id)

    suspend fun followUp(id: String, text: String, alias: String? = null) {
        clientProvider(alias)?.followUp(id, FollowUpRequest(prompt = fr.lawmight.cursoragents.data.api.Prompt(text)))
    }

    suspend fun stop(id: String, alias: String? = null) {
        clientProvider(alias)?.stopAgent(id)
    }

    suspend fun delete(id: String, alias: String? = null) {
        clientProvider(alias)?.deleteAgent(id)
    }
}
