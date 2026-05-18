package fr.lawmight.cursoragents.data.repository

import fr.lawmight.cursoragents.api.CursorApiClient
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.ConversationMessage
import fr.lawmight.cursoragents.api.models.FollowUpRequest
import fr.lawmight.cursoragents.api.models.LaunchAgentRequest
import fr.lawmight.cursoragents.api.models.Prompt
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
        c.listAgents().onSuccess { _agents.value = it.agents }
    }

    suspend fun launch(
        req: LaunchAgentRequest,
        alias: String? = null,
    ): Agent? = clientProvider(alias)?.launchAgent(req)?.getOrNull()

    suspend fun conversation(
        id: String,
        alias: String? = null,
    ): List<ConversationMessage>? = clientProvider(alias)?.getAgentConversation(id)?.getOrNull()

    suspend fun followUp(
        id: String,
        text: String,
        alias: String? = null,
    ) {
        clientProvider(alias)?.addFollowUp(id, FollowUpRequest(prompt = Prompt(text)))
    }

    suspend fun stop(
        id: String,
        alias: String? = null,
    ) {
        clientProvider(alias)?.stopAgent(id)?.getOrNull()
    }

    suspend fun delete(
        id: String,
        alias: String? = null,
    ) {
        clientProvider(alias)?.deleteAgent(id)?.getOrNull()
    }
}
