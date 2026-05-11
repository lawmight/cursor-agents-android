package fr.lawmight.cursoragents.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AgentStatus { CREATING, RUNNING, FINISHED, STOPPED, FAILED }

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
    val autoCreatePr: Boolean? = null,
    val openAsCursorGithubApp: Boolean? = null,
    val skipReviewerRequest: Boolean? = null,
)

@Serializable
data class Agent(
    val id: String,
    val name: String = "",
    val status: AgentStatus,
    val source: Source,
    val target: Target = Target(),
    val summary: String? = null,
    val createdAt: String,
)

@Serializable
data class AgentListResponse(
    val agents: List<Agent>,
    val nextCursor: String? = null,
)

@Serializable
data class ConversationMessage(
    val id: String,
    val type: String,
    val text: String,
)

@Serializable
data class AgentConversation(
    val id: String,
    val messages: List<ConversationMessage>,
)

@Serializable
data class ImageData(
    val data: String,
    val dimension: ImageDimension,
)

@Serializable
data class ImageDimension(val width: Int, val height: Int)

@Serializable
data class Prompt(
    val text: String,
    val images: List<ImageData>? = null,
)

@Serializable
data class LaunchAgentRequest(
    val prompt: Prompt,
    val model: String? = null,
    val source: Source,
    val target: Target? = null,
)

@Serializable
data class FollowUpRequest(val prompt: Prompt)

@Serializable
data class MeResponse(
    val apiKeyName: String,
    val createdAt: String,
    val userEmail: String,
)

@Serializable
data class ModelsResponse(val models: List<String>)

@Serializable
data class Repository(
    val owner: String,
    val name: String,
    val repository: String,
)

@Serializable
data class RepositoriesResponse(val repositories: List<Repository>)
