package fr.lawmight.cursoragents.data.api

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Suppress("TooManyFunctions")
class CursorApiClient private constructor(
    private val baseUrl: String,
    private val client: HttpClient,
) {
    constructor(
        apiKey: String,
        baseUrl: String = "https://api.cursor.com",
        engineFactory: HttpClientEngineFactory<*> = CIO,
    ) : this(baseUrl, buildClient(apiKey, engineFactory))

    internal constructor(
        apiKey: String,
        baseUrl: String,
        engine: HttpClientEngine,
    ) : this(baseUrl, buildClient(apiKey, engine))

    suspend fun me(): MeResponse = client.get("$baseUrl/v0/me").body()

    suspend fun listAgents(
        limit: Int? = null,
        cursor: String? = null,
    ): AgentListResponse =
        client.get("$baseUrl/v0/agents") {
            limit?.let { url.parameters.append("limit", it.toString()) }
            cursor?.let { url.parameters.append("cursor", it) }
        }.body()

    suspend fun getAgent(id: String): Agent = client.get("$baseUrl/v0/agents/$id").body()

    suspend fun getConversation(id: String): AgentConversation = client.get("$baseUrl/v0/agents/$id/conversation").body()

    suspend fun launchAgent(req: LaunchAgentRequest): Agent = client.post("$baseUrl/v0/agents") { setBody(req) }.body()

    suspend fun followUp(
        id: String,
        req: FollowUpRequest,
    ): Map<String, String> = client.post("$baseUrl/v0/agents/$id/followup") { setBody(req) }.body()

    suspend fun stopAgent(id: String): Map<String, String> = client.post("$baseUrl/v0/agents/$id/stop").body()

    suspend fun deleteAgent(id: String): Map<String, String> = client.delete("$baseUrl/v0/agents/$id").body()

    suspend fun listModels(): ModelsResponse = client.get("$baseUrl/v0/models").body()

    suspend fun listRepositories(): RepositoriesResponse = client.get("$baseUrl/v0/repositories").body()

    fun close() = client.close()

    private companion object {
        private val json =
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            }

        fun buildClient(
            apiKey: String,
            engineFactory: HttpClientEngineFactory<*>,
        ): HttpClient = HttpClient(engineFactory) { configure(apiKey) }

        fun buildClient(
            apiKey: String,
            engine: HttpClientEngine,
        ): HttpClient = HttpClient(engine) { configure(apiKey) }

        private fun HttpClientConfig<*>.configure(apiKey: String) {
            install(ContentNegotiation) { json(json) }
            install(Logging)
            defaultRequest {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
            }
            HttpResponseValidator {
                validateResponse { response ->
                    response.toCursorApiException()?.let { throw it }
                }
            }
        }
    }
}

private const val HTTP_ERROR_MIN_CODE = 400

private suspend fun HttpResponse.bodyAsTextOrEmpty(): String = runCatching { bodyAsText() }.getOrDefault("")

private suspend fun HttpResponse.toCursorApiException(): CursorApiException? =
    when (status) {
        HttpStatusCode.Unauthorized -> CursorApiException.Unauthorized
        HttpStatusCode.Forbidden -> CursorApiException.Forbidden
        HttpStatusCode.NotFound -> CursorApiException.NotFound
        HttpStatusCode.TooManyRequests -> CursorApiException.RateLimited
        else ->
            if (status.value >= HTTP_ERROR_MIN_CODE) {
                CursorApiException.Unexpected(status.value, bodyAsTextOrEmpty())
            } else {
                null
            }
    }
