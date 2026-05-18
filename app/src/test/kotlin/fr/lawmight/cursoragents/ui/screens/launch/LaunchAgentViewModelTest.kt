package fr.lawmight.cursoragents.ui.screens.launch

import app.cash.turbine.test
import fr.lawmight.cursoragents.api.CursorApiClient
import fr.lawmight.cursoragents.api.CursorApiError
import fr.lawmight.cursoragents.api.models.Agent
import fr.lawmight.cursoragents.api.models.AgentStatus
import fr.lawmight.cursoragents.api.models.LaunchAgentRequest
import fr.lawmight.cursoragents.api.models.Source
import fr.lawmight.cursoragents.api.models.Target
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.data.repository.AgentsRepository
import fr.lawmight.cursoragents.di.CursorApiClientFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LaunchAgentViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var keyStore: EncryptedKeyStore
    private lateinit var client: CursorApiClient
    private lateinit var repository: AgentsRepository
    private lateinit var factory: CursorApiClientFactory

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        keyStore =
            mockk {
                coEvery { get() } returns "cursor-key"
            }
        client =
            mockk {
                every { close() } just runs
            }
        repository =
            mockk {
                coEvery { refresh() } returns Unit
            }
        factory = CursorApiClientFactory { client }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submit validates empty repo`() =
        runTest {
            val viewModel = viewModel()

            viewModel.formState.test {
                assertEquals(LaunchAgentFormState(), awaitItem())

                viewModel.onPromptChange("Build launch screen")
                assertTrue(awaitItem().promptError == null)

                viewModel.submit()

                val invalid = awaitItem()
                assertEquals("Enter a GitHub repository.", invalid.repoError)
                assertFalse(invalid.canSubmit)
            }
        }

    @Test
    fun `submit validates empty prompt`() =
        runTest {
            val viewModel = viewModel()

            viewModel.formState.test {
                assertEquals(LaunchAgentFormState(), awaitItem())

                viewModel.onRepoChange("lawmight/cursor-agents-android")
                assertTrue(awaitItem().repoError == null)

                viewModel.submit()

                val invalid = awaitItem()
                assertEquals("Describe what the agent should do.", invalid.promptError)
                assertFalse(invalid.canSubmit)
            }
        }

    @Test
    fun `submit validates short prompt`() =
        runTest {
            val viewModel = viewModel()

            viewModel.formState.test {
                assertEquals(LaunchAgentFormState(), awaitItem())

                viewModel.onRepoChange("lawmight/cursor-agents-android")
                assertTrue(awaitItem().repoError == null)

                viewModel.onPromptChange("too short")
                val shortPrompt = awaitItem()
                assertEquals("Prompt must be at least 10 characters.", shortPrompt.promptError)
                assertFalse(shortPrompt.canSubmit)
            }
        }

    @Test
    fun `successful submit transitions to success and refreshes agents`() =
        runTest {
            val requestSlot = slot<LaunchAgentRequest>()
            coEvery { client.createAgent(capture(requestSlot)) } returns Result.success(agent("agent-123"))
            val viewModel = viewModel()

            viewModel.formState.test {
                assertEquals(LaunchAgentFormState(), awaitItem())

                viewModel.onRepoChange("lawmight/cursor-agents-android")
                assertTrue(awaitItem().canSubmit.not())
                viewModel.onBranchChange("feature")
                assertEquals("feature", awaitItem().branch)
                viewModel.onPromptChange("Build the launch agent screen")
                assertTrue(awaitItem().canSubmit)
                viewModel.onModelChange(ModelSelection.Gpt5Codex)
                assertEquals(ModelSelection.Gpt5Codex, awaitItem().model)

                viewModel.submit()
                assertTrue(awaitItem().launchResult is LaunchResult.Submitting)
                testDispatcher.scheduler.advanceUntilIdle()

                val success = awaitItem().launchResult as LaunchResult.Success
                assertEquals("agent-123", success.agentId)
            }

            assertEquals("https://github.com/lawmight/cursor-agents-android", requestSlot.captured.source.repository)
            assertEquals("feature", requestSlot.captured.source.ref)
            assertEquals("Build the launch agent screen", requestSlot.captured.prompt.text)
            assertEquals("gpt-5-codex", requestSlot.captured.model)
            coVerify(exactly = 1) { repository.refresh() }
        }

    @Test
    fun `api error surfaces as error state`() =
        runTest {
            coEvery { client.createAgent(any()) } returns
                Result.failure(CursorApiError.RateLimited(RETRY_AFTER_SECONDS))
            val viewModel = viewModel()

            viewModel.formState.test {
                assertEquals(LaunchAgentFormState(), awaitItem())

                viewModel.onRepoChange("https://github.com/lawmight/cursor-agents-android")
                awaitItem()
                viewModel.onPromptChange("Build the launch agent screen")
                awaitItem()

                viewModel.submit()
                assertTrue(awaitItem().launchResult is LaunchResult.Submitting)
                testDispatcher.scheduler.advanceUntilIdle()

                val error = awaitItem().launchResult as LaunchResult.Error
                assertEquals("Cursor API rate limit exceeded.", error.message)
                assertTrue(error.retryable)
            }

            coVerify(exactly = 0) { repository.refresh() }
        }

    private fun viewModel(): LaunchAgentViewModel =
        LaunchAgentViewModel(
            keyStore = keyStore,
            clientFactory = factory,
            agentsRepository = repository,
            ioDispatcher = testDispatcher,
        )

    private fun agent(id: String): Agent =
        Agent(
            id = id,
            status = AgentStatus.CREATING,
            source = Source(repository = "https://github.com/lawmight/cursor-agents-android"),
            target = Target(branchName = "cursor/$id"),
            createdAt = "2026-05-18T00:00:00Z",
        )

    private companion object {
        const val RETRY_AFTER_SECONDS = 30
    }
}
