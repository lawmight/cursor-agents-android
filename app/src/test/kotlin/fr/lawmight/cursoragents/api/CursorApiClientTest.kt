package fr.lawmight.cursoragents.api

import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.LaunchAgentRequest
import fr.lawmight.cursoragents.api.models.Prompt
import fr.lawmight.cursoragents.api.models.Source
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class CursorApiClientTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

    @Test
    fun `getMe returns decoded profile`() =
        runTest {
            val client =
                testClient { request ->
                    assertEquals("Bearer test-token", request.headers[HttpHeaders.Authorization])
                    assertEquals("/v0/me", request.url.encodedPath)
                    jsonResponse(
                        """
                        {
                          "apiKeyName": "Android dev",
                          "userEmail": "dev@example.com",
                          "createdAt": "2026-05-18T00:00:00Z"
                        }
                        """.trimIndent(),
                    )
                }

            val me = client.getMe().getOrThrow()

            assertEquals("Android dev", me.apiKeyName)
            assertEquals("dev@example.com", me.userEmail)
            assertEquals("2026-05-18T00:00:00Z", me.createdAt)
        }

    @Test
    fun `listAgents returns agents and next cursor`() =
        runTest {
            val client =
                testClient { request ->
                    assertEquals("/v0/agents", request.url.encodedPath)
                    assertEquals("2", request.url.parameters["limit"])
                    assertEquals("cursor-1", request.url.parameters["cursor"])
                    jsonResponse(
                        """
                        {
                          "agents": [
                            {
                              "id": "agent-1",
                              "status": "RUNNING",
                              "name": "Fix tests",
                              "source": { "repository": "lawmight/cursor-agents-android", "ref": "main" },
                              "target": { "branchName": "cursor/fix-tests" },
                              "summary": "Fix failing tests",
                              "createdAt": "2026-05-18T00:00:00Z",
                              "branchName": "cursor/fix-tests"
                            }
                          ],
                          "nextCursor": "cursor-2"
                        }
                        """.trimIndent(),
                    )
                }

            val page = client.listAgents(limit = 2, cursor = "cursor-1").getOrThrow()

            assertEquals("cursor-2", page.nextCursor)
            assertEquals(1, page.agents.size)
            assertEquals("agent-1", page.agents.single().id)
            assertEquals(AgentStatus.RUNNING, page.agents.single().status)
        }

    @Test
    fun `launchAgent returns created agent`() =
        runTest {
            val client =
                testClient { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/v0/agents", request.url.encodedPath)
                    jsonResponse(
                        """
                        {
                          "id": "agent-2",
                          "status": "CREATING",
                          "source": { "repository": "lawmight/cursor-agents-android", "ref": "main" },
                          "target": { "branchName": "cursor/new-agent" },
                          "createdAt": "2026-05-18T00:05:00Z"
                        }
                        """.trimIndent(),
                    )
                }

            val agent =
                client
                    .launchAgent(
                        LaunchAgentRequest(
                            prompt = Prompt("Build CUR-19"),
                            source = Source(repository = "lawmight/cursor-agents-android", ref = "main"),
                        ),
                    )
                    .getOrThrow()

            assertEquals("agent-2", agent.id)
            assertEquals(AgentStatus.CREATING, agent.status)
        }

    @Test
    fun `401 maps to Unauthorized`() =
        runTest {
            val client = testClient { jsonResponse("""{}""", HttpStatusCode.Unauthorized) }

            val error = client.getMe().exceptionOrNull()

            assertSame(CursorApiError.Unauthorized, error)
        }

    @Test
    fun `429 maps Retry-After to RateLimited`() =
        runTest {
            val client =
                testClient {
                    respond(
                        content = """{}""",
                        status = HttpStatusCode.TooManyRequests,
                        headers =
                            headersOf(
                                HttpHeaders.ContentType to listOf("application/json"),
                                HttpHeaders.RetryAfter to listOf("60"),
                            ),
                    )
                }

            val error = client.listAgents().exceptionOrNull()

            assertEquals(CursorApiError.RateLimited(retryAfterSec = 60), error)
        }

    @Test
    fun `5xx maps to ServerError`() =
        runTest {
            val client = testClient { jsonResponse("""{}""", HttpStatusCode.InternalServerError) }

            val error = client.getMe().exceptionOrNull()

            assertEquals(CursorApiError.ServerError(code = 500), error)
        }

    @Test
    fun `malformed JSON maps to DecodeError`() =
        runTest {
            val client = testClient { jsonResponse("""{"apiKeyName":""") }

            val error = client.getMe().exceptionOrNull()

            assertNotNull(error)
            assertTrue(error is CursorApiError.DecodeError)
        }

    private fun testClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): CursorApiClient {
        val httpClient =
            HttpClient(MockEngine(handler)) {
                install(ContentNegotiation) {
                    json(json)
                }
            }
        return CursorApiClient(token = "test-token", httpClient = httpClient)
    }

    private fun MockRequestHandleScope.jsonResponse(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): HttpResponseData =
        respond(
            content = body,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
}
