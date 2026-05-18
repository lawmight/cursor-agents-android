package fr.lawmight.cursoragents.ui.screens.agentlist

import app.cash.turbine.test
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.api.models.Target
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AgentListViewModelTest {
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
    fun `empty state transitions to content when agentsFlow emits agents`() =
        runTest {
            val agents = MutableStateFlow(emptyList<Agent>())
            val repository = repositoryWith(agents)
            val viewModel = viewModelWith(repository)

            viewModel.uiState.test {
                assertEquals(AgentListUiState.Loading, awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()
                assertEquals(AgentListUiState.Empty(), awaitItem())

                agents.value = listOf(agent(id = "agt_1234567890"))
                testDispatcher.scheduler.advanceUntilIdle()

                val content = awaitItem() as AgentListUiState.Content
                assertEquals("agt_1234...", content.agents.single().idLabel)
                assertEquals("cursor-agents-android", content.agents.single().repositoryName)
                assertEquals("cursor/agt_1234567890", content.agents.single().branchName)
            }
        }

    @Test
    fun `refresh failure emits retryable error state`() =
        runTest {
            val agents = MutableStateFlow(emptyList<Agent>())
            val repository = repositoryWith(agents)
            coEvery { repository.refresh() } throws IOException("offline")
            val viewModel = viewModelWith(repository)

            viewModel.uiState.test {
                assertEquals(AgentListUiState.Loading, awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()

                val error = awaitItem() as AgentListUiState.Error
                assertEquals("offline", error.message)
                assertTrue(error.canRetry)
            }
        }

    @Test
    fun `manual refresh calls repository and updates content from agentsFlow`() =
        runTest {
            val agents = MutableStateFlow(listOf(agent(id = "first")))
            val repository = repositoryWith(agents)
            var refreshCount = 0
            coEvery { repository.refresh() } coAnswers {
                refreshCount += 1
                if (refreshCount == 2) {
                    agents.value = listOf(agent(id = "second"))
                }
            }
            val viewModel = viewModelWith(repository)

            viewModel.uiState.test {
                assertEquals(AgentListUiState.Loading, awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()

                var content = awaitContent()
                assertEquals("first", content.agents.single().agent.id)

                viewModel.refresh()
                testDispatcher.scheduler.advanceUntilIdle()

                content = awaitContent()
                assertEquals("second", content.agents.single().agent.id)
                coVerify(exactly = 2) { repository.refresh() }
            }
        }

    private suspend fun app.cash.turbine.ReceiveTurbine<AgentListUiState>.awaitContent(): AgentListUiState.Content {
        while (true) {
            val item = awaitItem()
            if (item is AgentListUiState.Content && !item.isRefreshing) {
                return item
            }
        }
    }

    private fun repositoryWith(agents: MutableStateFlow<List<Agent>>): AgentsRepository =
        mockk {
            every { agentsFlow } returns agents
            coEvery { refresh() } returns Unit
        }

    private fun viewModelWith(repository: AgentsRepository): AgentListViewModel =
        AgentListViewModel(
            repository = repository,
            ioDispatcher = testDispatcher,
        )

    private fun agent(id: String): Agent =
        Agent(
            id = id,
            status = AgentStatus.RUNNING,
            source = Source(repository = "https://github.com/lawmight/cursor-agents-android", ref = "main"),
            target = Target(branchName = "cursor/$id"),
            createdAt = "2026-05-18T00:00:00Z",
        )
}
