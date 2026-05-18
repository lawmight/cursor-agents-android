package fr.lawmight.cursoragents.data

import app.cash.turbine.test
import fr.lawmight.cursoragents.data.api.Agent
import fr.lawmight.cursoragents.data.api.AgentStatus
import fr.lawmight.cursoragents.data.api.FollowUpRequest
import fr.lawmight.cursoragents.data.api.LaunchAgentRequest
import fr.lawmight.cursoragents.data.api.Prompt
import fr.lawmight.cursoragents.data.api.Source
import fr.lawmight.cursoragents.data.api.Target
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
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
                    FakeAgentsApi(listResponses = ArrayDeque(listOf(listOf(refreshedAgent)))),
                    Unit,
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
            val repository =
                AgentsRepository(
                    FakeAgentsApi(listResponses = ArrayDeque(listOf(listOf(firstAgent), listOf(secondAgent)))),
                    Unit,
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
                    FakeAgentsApi(
                        listAgentHandler = {
                            attempts += 1
                            attemptTimes += currentTime
                            if (attempts < SUCCESSFUL_ATTEMPT) {
                                throw IOException("offline")
                            }
                            listOf(successfulAgent)
                        },
                    ),
                    Unit,
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
                    FakeAgentsApi(
                        listAgentHandler = {
                            attempts += 1
                            listOf(agent("agent-$attempts"))
                        },
                    ),
                    Unit,
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
                    FakeAgentsApi(launchAgentHandler = { launchedAgent }),
                    Unit,
                )

            val result = repository.launch(launchRequest())

            assertTrue(result.isSuccess)
            assertEquals(launchedAgent, result.getOrNull())
            assertEquals(listOf(launchedAgent), repository.agents.value)
        }

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
        const val POLLING_INTERVAL_MS = 1_000L
        const val FIRST_BACKOFF_MS = 5_000L
        const val SECOND_BACKOFF_MS = 10_000L
        const val MAX_BACKOFF_MS = 30_000L
        const val TOTAL_BACKOFF_MS = FIRST_BACKOFF_MS + SECOND_BACKOFF_MS + MAX_BACKOFF_MS
        const val CANCELLED_POLLING_ADVANCE_MS = POLLING_INTERVAL_MS * 3
        const val SUCCESSFUL_ATTEMPT = 4
    }
}

private class FakeAgentsApi(
    private val listResponses: ArrayDeque<List<Agent>> = ArrayDeque(),
    private val listAgentHandler: (suspend () -> List<Agent>)? = null,
    private val launchAgentHandler: (suspend (LaunchAgentRequest) -> Agent)? = null,
) : AgentsApi {
    override suspend fun listAgents(): List<Agent> = listAgentHandler?.invoke() ?: listResponses.removeFirst()

    override suspend fun launchAgent(req: LaunchAgentRequest): Agent = checkNotNull(launchAgentHandler).invoke(req)

    override suspend fun stopAgent(id: String) = Unit

    override suspend fun deleteAgent(id: String) = Unit

    override suspend fun followUp(
        id: String,
        req: FollowUpRequest,
    ) = Unit
}
