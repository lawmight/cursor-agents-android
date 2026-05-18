package fr.lawmight.cursoragents.data

import app.cash.turbine.test
import fr.lawmight.cursoragents.data.api.Agent
import fr.lawmight.cursoragents.data.api.AgentListResponse
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.data.api.CursorApiClient
import fr.lawmight.cursoragents.data.api.LaunchAgentRequest
import fr.lawmight.cursoragents.data.api.Prompt
import fr.lawmight.cursoragents.data.api.Source
import fr.lawmight.cursoragents.data.api.Target
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AgentsRepositoryTest {
    @Test
    fun `refresh updates flow`() =
        runTest {
            val refreshedAgent = agent("agent-1")
            val repository =
                AgentsRepository(
                    clientWith {
                        respondJson(listResponse(refreshedAgent))
                    },
                )

            repository.agents.test {
                assertEquals(emptyList<Agent>(), awaitItem())

                repository.refresh()

                assertEquals(listOf(refreshedAgent), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `polling ticks refresh at requested interval`() =
        runTest {
            val firstAgent = agent("agent-1", AgentStatus.CREATING)
            val secondAgent = agent("agent-1", AgentStatus.RUNNING)
            val responses = ArrayDeque(listOf(listResponse(firstAgent), listResponse(secondAgent)))
            val repository =
                AgentsRepository(
                    clientWith {
                        respondJson(responses.removeFirst())
                    },
                )

            val job = repository.startPolling(intervalMs = POLLING_INTERVAL_MS, scope = this)
            runCurrent()

            assertEquals(listOf(firstAgent), repository.agents.value)

            advanceTimeBy(POLLING_INTERVAL_MS)
            runCurrent()

            assertEquals(listOf(secondAgent), repository.agents.value)
            job.cancel()
        }

    @Test
    fun `polling backs off on error then resets after success`() =
        runTest {
            val successfulAgent = agent("agent-1")
            var attempts = 0
            val attemptTimes = mutableListOf<Long>()
            val repository =
                AgentsRepository(
                    clientWith {
                        attempts += 1
                        attemptTimes += currentTime
                        if (attempts < SUCCESSFUL_ATTEMPT) {
                            throw IOException("offline")
                        }
                        respondJson(listResponse(successfulAgent))
                    },
                )

            val job = repository.startPolling(intervalMs = POLLING_INTERVAL_MS, scope = this)
            runCurrent()
            advanceTimeBy(FIRST_BACKOFF_MS)
            runCurrent()
            advanceTimeBy(SECOND_BACKOFF_MS)
            runCurrent()
            advanceTimeBy(MAX_BACKOFF_MS)
            runCurrent()

            assertEquals(
                listOf(
                    0L,
                    FIRST_BACKOFF_MS,
                    FIRST_BACKOFF_MS + SECOND_BACKOFF_MS,
                    TOTAL_BACKOFF_MS,
                ),
                attemptTimes,
            )
            assertEquals(listOf(successfulAgent), repository.agents.value)

            advanceTimeBy(POLLING_INTERVAL_MS)
            runCurrent()

            assertEquals(TOTAL_BACKOFF_MS + POLLING_INTERVAL_MS, attemptTimes.last())
            job.cancel()
        }

    @Test
    fun `stop cancels polling job cleanly`() =
        runTest {
            var attempts = 0
            val repository =
                AgentsRepository(
                    clientWith {
                        attempts += 1
                        respondJson(listResponse(agent("agent-$attempts")))
                    },
                )

            val job = repository.startPolling(intervalMs = POLLING_INTERVAL_MS, scope = this)
            runCurrent()

            job.cancel()
            advanceTimeBy(CANCELLED_POLLING_ADVANCE_MS)
            runCurrent()

            assertEquals(1, attempts)
            assertTrue(job.isCancelled)
        }

    @Test
    fun `launch optimistically inserts launched agent into flow`() =
        runTest {
            val launchedAgent = agent("agent-2")
            val repository =
                AgentsRepository(
                    clientWith { request ->
                        assertEquals(HttpMethod.Post, request.method)
                        assertEquals("/v0/agents", request.url.encodedPath)
                        respondJson(json.encodeToString(launchedAgent))
                    },
                )

            val result = repository.launch(launchRequest())

            assertTrue(result.isSuccess)
            assertEquals(launchedAgent, result.getOrNull())
            assertEquals(listOf(launchedAgent), repository.agents.value)
        }

    private fun clientWith(handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): CursorApiClient =
        CursorApiClient(
            apiKey = "cursor-key",
            baseUrl = BASE_URL,
            engine = MockEngine(handler),
        )

    private fun MockRequestHandleScope.respondJson(content: String): HttpResponseData =
        respond(
            content = content,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )

    private fun listResponse(vararg agents: Agent): String = json.encodeToString(AgentListResponse(agents.toList()))

    private fun agent(
        id: String,
        status: AgentStatus = AgentStatus.RUNNING,
    ): Agent =
        Agent(
            id = id,
            name = "Agent $id",
            status = status,
            source = Source(repository = "lawmight/cursor-agents-android"),
            target = Target(branchName = "branch-$id"),
            createdAt = "2026-05-18T00:00:00Z",
        )

    private fun launchRequest(): LaunchAgentRequest =
        LaunchAgentRequest(
            prompt = Prompt(text = "Build feature"),
            source = Source(repository = "lawmight/cursor-agents-android"),
            target = Target(branchName = "feature"),
        )

    private companion object {
        const val BASE_URL = "https://api.cursor.test"
        const val POLLING_INTERVAL_MS = 1_000L
        const val FIRST_BACKOFF_MS = 5_000L
        const val SECOND_BACKOFF_MS = 10_000L
        const val MAX_BACKOFF_MS = 30_000L
        const val TOTAL_BACKOFF_MS = FIRST_BACKOFF_MS + SECOND_BACKOFF_MS + MAX_BACKOFF_MS
        const val CANCELLED_POLLING_ADVANCE_MS = POLLING_INTERVAL_MS * 3
        const val SUCCESSFUL_ATTEMPT = 4

        val json =
            Json {
                encodeDefaults = true
            }
    }
}
