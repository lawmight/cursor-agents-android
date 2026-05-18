package fr.lawmight.cursoragents.ui.screens.agentdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.ConversationMessage
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.api.models.Target
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AgentDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading transitions to content after agent and conversation load`() =
        runTest {
            val repository = repositoryWith(agent(status = AgentStatus.RUNNING))
            coEvery { repository.refreshAgent(AGENT_ID) } returns agent(status = AgentStatus.RUNNING)
            coEvery { repository.conversation(AGENT_ID) } returns listOf(message("m1", "user", "Build CUR-20"))

            val viewModel = viewModelWith(repository)

            viewModel.uiState.test {
                assertEquals(AgentDetailUiState.Loading, awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()

                val content = awaitContent()
                assertEquals(AgentStatus.RUNNING, content.agent.status)
                assertEquals("Build CUR-20", content.turns.single().body)
                assertFalse(content.inputEnabled)
            }
        }

    @Test
    fun `sendFollowup appends optimistic turn calls api and refreshes`() =
        runTest {
            val initialAgent = agent(status = AgentStatus.FINISHED)
            val runningAgent = agent(status = AgentStatus.RUNNING)
            val repository = repositoryWith(initialAgent)
            val followUpStarted = CompletableDeferred<Unit>()
            val finishFollowUp = CompletableDeferred<Unit>()
            coEvery { repository.refreshAgent(AGENT_ID) } returnsMany listOf(initialAgent, runningAgent)
            coEvery { repository.conversation(AGENT_ID) } returnsMany
                listOf(
                    listOf(message("m1", "assistant", "Ready for follow-up")),
                    listOf(message("m1", "assistant", "Ready for follow-up")),
                )
            coEvery { repository.followUp(AGENT_ID, FOLLOWUP_TEXT) } coAnswers {
                followUpStarted.complete(Unit)
                finishFollowUp.await()
            }

            val viewModel = viewModelWith(repository)

            viewModel.uiState.test {
                assertEquals(AgentDetailUiState.Loading, awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()
                awaitContent()

                viewModel.onFollowupChange(FOLLOWUP_TEXT)
                assertEquals(FOLLOWUP_TEXT, awaitContent().followupText)

                viewModel.sendFollowup(FOLLOWUP_TEXT)
                runCurrent()
                followUpStarted.await()

                val sending = awaitContent()
                assertTrue(sending.sending)
                assertEquals("", sending.followupText)
                assertEquals(FOLLOWUP_TEXT, sending.turns.last().body)
                assertTrue(sending.turns.last().isOptimistic)

                finishFollowUp.complete(Unit)
                testDispatcher.scheduler.advanceUntilIdle()

                val refreshed = awaitContent()
                assertFalse(refreshed.sending)
                assertEquals(AgentStatus.RUNNING, refreshed.agent.status)
                assertEquals(FOLLOWUP_TEXT, refreshed.turns.last().body)
            }

            coVerify(exactly = 1) { repository.followUp(AGENT_ID, FOLLOWUP_TEXT) }
            coVerify(exactly = 2) { repository.refreshAgent(AGENT_ID) }
            coVerify(exactly = 2) { repository.conversation(AGENT_ID) }
        }

    @Test
    fun `sendFollowup error restores input and removes optimistic turn`() =
        runTest {
            val repository = repositoryWith(agent(status = AgentStatus.FINISHED))
            coEvery { repository.refreshAgent(AGENT_ID) } returns agent(status = AgentStatus.FINISHED)
            coEvery { repository.conversation(AGENT_ID) } returns listOf(message("m1", "assistant", "Ready"))
            coEvery { repository.followUp(AGENT_ID, FOLLOWUP_TEXT) } throws IOException("offline")

            val viewModel = viewModelWith(repository)

            viewModel.uiState.test {
                assertEquals(AgentDetailUiState.Loading, awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()
                awaitContent()

                viewModel.onFollowupChange(FOLLOWUP_TEXT)
                awaitContent()

                viewModel.sendFollowup(FOLLOWUP_TEXT)
                testDispatcher.scheduler.advanceUntilIdle()

                val errorState = awaitContent { it.sendError != null }
                assertFalse(errorState.sending)
                assertEquals(FOLLOWUP_TEXT, errorState.followupText)
                assertEquals("offline", errorState.sendError)
                assertEquals(listOf("Ready"), errorState.turns.map { it.body })
            }
        }

    private suspend fun ReceiveTurbine<AgentDetailUiState>.awaitContent(
        predicate: (AgentDetailUiState.Content) -> Boolean = { true },
    ): AgentDetailUiState.Content {
        while (true) {
            val item = awaitItem()
            if (item is AgentDetailUiState.Content && predicate(item)) return item
        }
    }

    private fun repositoryWith(agent: Agent): AgentsRepository =
        mockk {
            every { agentFlow(AGENT_ID) } returns MutableStateFlow(agent)
        }

    private fun viewModelWith(repository: AgentsRepository): AgentDetailViewModel =
        AgentDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to AGENT_ID)),
            agentsRepository = repository,
            ioDispatcher = testDispatcher,
        )

    private fun agent(status: AgentStatus): Agent =
        Agent(
            id = AGENT_ID,
            status = status,
            source = Source(repository = "https://github.com/lawmight/cursor-agents-android", ref = "main"),
            target = Target(branchName = "cursor/agent-detail"),
            createdAt = "2026-05-18T00:00:00Z",
        )

    private fun message(
        id: String,
        type: String,
        text: String,
    ): ConversationMessage =
        ConversationMessage(
            id = id,
            type = type,
            text = text,
            createdAt = "2026-05-18T00:00:00Z",
        )

    private companion object {
        const val AGENT_ID = "agent-1"
        const val FOLLOWUP_TEXT = "Please add tests"
    }
}
