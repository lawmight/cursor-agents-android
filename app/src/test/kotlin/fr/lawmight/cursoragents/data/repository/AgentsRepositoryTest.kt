package fr.lawmight.cursoragents.data.repository

import app.cash.turbine.test
import fr.lawmight.cursoragents.api.CursorApiClient
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.di.CursorApiClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

private typealias MockHandler =
    suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
class AgentsRepositoryTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    @Test
    fun `refresh fetches all pages with encrypted key and updates agentsFlow`() =
        runTest {
            val requestedCursors = mutableListOf<String?>()
            val repository =
                repositoryWith(scope = backgroundScope) { request ->
                    assertEquals("Bearer stored-token", request.headers[HttpHeaders.Authorization])
                    requestedCursors += request.url.parameters["cursor"]
                    when (request.url.parameters["cursor"]) {
                        null -> agentsResponse(agentJson("agent-1"), nextCursor = "next-page")
                        "next-page" -> agentsResponse(agentJson("agent-2"))
                        else -> error("Unexpected cursor ${request.url.parameters["cursor"]}")
                    }
                }

            repository.agentsFlow.test {
                assertEquals(emptyList<Agent>(), awaitItem())

                repository.refresh()

                assertEquals(listOf("agent-1", "agent-2"), awaitItem().map { it.id })
                assertEquals(listOf(null, "next-page"), requestedCursors)
            }
        }

    @Test
    fun `agentFlow emits matching cached agent after refresh`() =
        runTest {
            val repository =
                repositoryWith(scope = backgroundScope) {
                    agentsResponse(agentJson("agent-1"), agentJson("agent-2"))
                }

            repository.agentFlow("agent-2").test {
                repository.refresh()

                assertEquals("agent-2", awaitItem().id)
            }
        }

    @Test
    fun `polling refreshes agents on the configured interval`() =
        runTest {
            var requestCount = 0
            val repository =
                repositoryWith(
                    scope = backgroundScope,
                    pollingIntervalMillis = 1_000L,
                ) {
                    requestCount += 1
                    agentsResponse(agentJson("agent-$requestCount"))
                }

            repository.agentsFlow.test {
                assertEquals(emptyList<Agent>(), awaitItem())

                runCurrent()
                assertEquals("agent-1", awaitItem().single().id)

                advanceTimeBy(1_000L)
                runCurrent()
                assertEquals("agent-2", awaitItem().single().id)
            }
        }

    private fun repositoryWith(
        keyStore: EncryptedKeyStore = FakeEncryptedKeyStore("stored-token"),
        scope: CoroutineScope,
        pollingIntervalMillis: Long = Long.MAX_VALUE,
        handler: MockHandler,
    ): AgentsRepository {
        val factory =
            CursorApiClientFactory { token ->
                CursorApiClient(
                    token = token,
                    httpClient =
                        HttpClient(MockEngine(handler)) {
                            install(ContentNegotiation) {
                                json(json)
                            }
                        },
                )
            }
        return AgentsRepository(
            keyStore = keyStore,
            clientFactory = factory,
            scope = scope,
            pollingIntervalMillis = pollingIntervalMillis,
        )
    }

    private fun MockRequestHandleScope.agentsResponse(
        vararg agents: String,
        nextCursor: String? = null,
    ): HttpResponseData =
        respond(
            content =
                buildString {
                    append("""{"agents":[""")
                    append(agents.joinToString(","))
                    append("]")
                    nextCursor?.let { append(",\"nextCursor\":\"$it\"") }
                    append("}")
                },
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )

    private fun agentJson(id: String): String =
        """
        {
          "id": "$id",
          "status": "RUNNING",
          "source": { "repository": "lawmight/cursor-agents-android", "ref": "main" },
          "target": { "branchName": "cursor/$id" },
          "createdAt": "2026-05-18T00:00:00Z"
        }
        """.trimIndent()

    private class FakeEncryptedKeyStore(
        private var apiKey: String?,
    ) : EncryptedKeyStore {
        override suspend fun put(key: String) {
            apiKey = key
        }

        override suspend fun get(): String? = apiKey

        override suspend fun clear() {
            apiKey = null
        }
    }
}
