package fr.lawmight.cursoragents.api

import fr.lawmight.cursoragents.BuildConfig
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.ConversationMessage
import fr.lawmight.cursoragents.api.models.FollowUpRequest
import fr.lawmight.cursoragents.api.models.LaunchAgentRequest
import fr.lawmight.cursoragents.api.models.Me
import fr.lawmight.cursoragents.api.models.ModelsResponse
import fr.lawmight.cursoragents.api.models.PaginatedAgents
import fr.lawmight.cursoragents.api.models.RepositoriesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

class CursorApiClient(
    private val token: String,
    private val httpClient: HttpClient = defaultClient(token),
) {
    suspend fun listAgents(
        limit: Int = 20,
        cursor: String? = null,
    ): Result<PaginatedAgents> =
        request {
            httpClient.get(endpoint("agents")) {
                authorize()
                parameter("limit", limit)
                cursor?.let { parameter("cursor", it) }
            }
        }

    suspend fun getAgent(id: String): Result<Agent> =
        request {
            httpClient.get(endpoint("agents/$id")) {
                authorize()
            }
        }

    suspend fun getAgentConversation(id: String): Result<List<ConversationMessage>> =
        request {
            httpClient.get(endpoint("agents/$id/conversation")) {
                authorize()
            }
        }

    suspend fun launchAgent(req: LaunchAgentRequest): Result<Agent> =
        request {
            httpClient.post(endpoint("agents")) {
                authorize()
                contentType(ContentType.Application.Json)
                setBody(req)
            }
        }

    suspend fun addFollowUp(
        id: String,
        req: FollowUpRequest,
    ): Result<Unit> =
        requestUnit {
            httpClient.post(endpoint("agents/$id/followup")) {
                authorize()
                contentType(ContentType.Application.Json)
                setBody(req)
            }
        }

    suspend fun stopAgent(id: String): Result<Unit> =
        requestUnit {
            httpClient.post(endpoint("agents/$id/stop")) {
                authorize()
            }
        }

    suspend fun deleteAgent(id: String): Result<Unit> =
        requestUnit {
            httpClient.delete(endpoint("agents/$id")) {
                authorize()
            }
        }

    suspend fun getMe(): Result<Me> =
        request {
            httpClient.get(endpoint("me")) {
                authorize()
            }
        }

    suspend fun listModels(): Result<ModelsResponse> =
        request {
            httpClient.get(endpoint("models")) {
                authorize()
            }
        }

    suspend fun listRepositories(): Result<RepositoriesResponse> =
        request {
            httpClient.get(endpoint("repositories")) {
                authorize()
            }
        }

    fun close() = httpClient.close()

    private fun endpoint(path: String): String = "${BuildConfig.CURSOR_API_BASE}/$path"

    private fun io.ktor.client.request.HttpRequestBuilder.authorize() {
        header(HttpHeaders.Authorization, "Bearer $token")
    }

    private suspend inline fun <reified T> request(crossinline call: suspend () -> HttpResponse): Result<T> =
        wrap {
            val response = call()
            response.toApiError()?.let { return@wrap Result.failure(it) }
            try {
                Result.success(response.body())
            } catch (cause: JsonConvertException) {
                Result.failure(CursorApiError.DecodeError(cause))
            } catch (cause: SerializationException) {
                Result.failure(CursorApiError.DecodeError(cause))
            }
        }

    private suspend fun requestUnit(call: suspend () -> HttpResponse): Result<Unit> =
        wrap {
            val response = call()
            response.toApiError()?.let { return@wrap Result.failure(it) }
            Result.success(Unit)
        }

    private suspend fun <T> wrap(block: suspend () -> Result<T>): Result<T> =
        try {
            block()
        } catch (cause: CancellationException) {
            throw cause
        } catch (cause: JsonConvertException) {
            Result.failure(CursorApiError.DecodeError(cause))
        } catch (cause: SerializationException) {
            Result.failure(CursorApiError.DecodeError(cause))
        } catch (cause: IOException) {
            Result.failure(CursorApiError.NetworkError(cause))
        }

    private fun HttpResponse.toApiError(): CursorApiError? =
        when {
            status.isSuccess() -> null
            status == HttpStatusCode.Unauthorized -> CursorApiError.Unauthorized
            status == HttpStatusCode.Forbidden -> CursorApiError.Forbidden
            status == HttpStatusCode.NotFound -> CursorApiError.NotFound
            status == HttpStatusCode.TooManyRequests ->
                CursorApiError.RateLimited(headers[HttpHeaders.RetryAfter]?.toIntOrNull())
            status.value >= SERVER_ERROR_MIN -> CursorApiError.ServerError(status.value)
            else -> CursorApiError.ServerError(status.value)
        }

    companion object {
        private const val SERVER_ERROR_MIN = 500

        @OptIn(ExperimentalSerializationApi::class)
        fun defaultClient(token: String): HttpClient =
            HttpClient(Android) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            explicitNulls = false
                        },
                    )
                }
                install(Logging) {
                    level = if (BuildConfig.DEBUG) LogLevel.HEADERS else LogLevel.NONE
                }
                defaultRequest {
                    url(BuildConfig.CURSOR_API_BASE)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                }
            }
    }
}
