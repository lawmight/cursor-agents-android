package fr.lawmight.cursoragents.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AgentStatus {
    @SerialName("CREATING")
    CREATING,

    @SerialName("RUNNING")
    RUNNING,

    @SerialName("FINISHED")
    FINISHED,

    @SerialName("STOPPED")
    STOPPED,

    @SerialName("FAILED")
    FAILED,
}

@Serializable
data class Agent(
    val id: String,
    val status: AgentStatus,
    val source: Source,
    val target: Target,
    val name: String? = null,
    val summary: String? = null,
    val createdAt: String,
    val branchName: String? = null,
)

@Serializable
data class Source(
    val repository: String,
    val ref: String? = null,
)

@Serializable
data class Target(
    val branchName: String? = null,
    val url: String? = null,
    val prUrl: String? = null,
)

@Serializable
data class ConversationMessage(
    val id: String,
    val type: String,
    val text: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class LaunchAgentRequest(
    val prompt: Prompt,
    val source: Source,
    val model: String? = null,
    val target: Target? = null,
)

@Serializable
data class Prompt(
    val text: String,
    val images: List<Image>? = null,
)

@Serializable
data class Image(
    val data: String,
    val dimension: Dimension? = null,
)

@Serializable
data class Dimension(
    val width: Int,
    val height: Int,
)

@Serializable
data class FollowUpRequest(
    val prompt: Prompt,
)

@Serializable
data class ModelsResponse(
    val models: List<String>,
)

@Serializable
data class RepositoriesResponse(
    val repositories: List<Repository>,
)

@Serializable
data class Repository(
    val owner: String,
    val name: String,
    val defaultBranch: String? = null,
    val repository: String,
)

@Serializable
data class Me(
    val apiKeyName: String,
    val userEmail: String,
    val createdAt: String,
)

@Serializable
data class PaginatedAgents(
    val agents: List<Agent>,
    val nextCursor: String? = null,
)
