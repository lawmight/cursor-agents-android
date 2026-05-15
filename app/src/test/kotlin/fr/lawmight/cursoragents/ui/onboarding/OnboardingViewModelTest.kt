package fr.lawmight.cursoragents.ui.onboarding

import app.cash.turbine.test
import fr.lawmight.cursoragents.data.api.CursorApiClient
import fr.lawmight.cursoragents.data.api.MeResponse
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.di.CursorApiClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var keyStore: EncryptedKeyStore
    private lateinit var messages: OnboardingErrorMessages

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        keyStore = mockk(relaxed = true)
        messages =
            mockk {
                every { unauthorized() } returns UNAUTHORIZED_MESSAGE
                every { forbidden() } returns FORBIDDEN_MESSAGE
                every { cursorUnavailable() } returns CURSOR_UNAVAILABLE_MESSAGE
                every { network() } returns NETWORK_MESSAGE
            }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful validation stores trimmed key and emits validated state`() =
        runTest {
            every { keyStore.read() } returns null
            val viewModel = viewModelWith(status = HttpStatusCode.OK)

            viewModel.state.test {
                assertEquals(OnboardingState.Idle(), awaitItem())

                viewModel.event(OnboardingEvent.OnValidate("  cursor-key  "))
                testDispatcher.scheduler.advanceUntilIdle()

                assertEquals(OnboardingState.Validating("cursor-key"), awaitItem())
                assertEquals(OnboardingState.Validated(ME_RESPONSE), awaitItem())
                verify { keyStore.save("cursor-key") }
            }
        }

    @Test
    fun `unauthorized validation failure uses invalid key message`() =
        runTest {
            assertValidationFailure(
                status = HttpStatusCode.Unauthorized,
                expectedMessage = UNAUTHORIZED_MESSAGE,
            )
        }

    @Test
    fun `forbidden validation failure uses permission message`() =
        runTest {
            assertValidationFailure(
                status = HttpStatusCode.Forbidden,
                expectedMessage = FORBIDDEN_MESSAGE,
            )
        }

    @Test
    fun `rate limited validation failure uses cursor unavailable message`() =
        runTest {
            assertValidationFailure(
                status = HttpStatusCode.TooManyRequests,
                expectedMessage = CURSOR_UNAVAILABLE_MESSAGE,
            )
        }

    @Test
    fun `network validation failure uses connection message`() =
        runTest {
            every { keyStore.read() } returns null
            val viewModel = viewModelWith(MockEngine { throw IOException("offline") })

            viewModel.state.test {
                assertEquals(OnboardingState.Idle(), awaitItem())

                viewModel.event(OnboardingEvent.OnValidate("cursor-key"))
                testDispatcher.scheduler.advanceUntilIdle()

                assertEquals(OnboardingState.Validating("cursor-key"), awaitItem())
                assertEquals(
                    OnboardingState.ValidationFailed(
                        keyDraft = "cursor-key",
                        message = NETWORK_MESSAGE,
                    ),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `saved key is validated on start and prefills draft while loading`() =
        runTest {
            every { keyStore.read() } returns "saved-key"
            val viewModel = viewModelWith(status = HttpStatusCode.OK)

            viewModel.state.test {
                assertEquals(OnboardingState.Idle(), awaitItem())
                testDispatcher.scheduler.advanceUntilIdle()

                assertEquals(OnboardingState.Validating("saved-key"), awaitItem())
                assertEquals(OnboardingState.Validated(ME_RESPONSE), awaitItem())
            }
        }

    private suspend fun assertValidationFailure(
        status: HttpStatusCode,
        expectedMessage: String,
    ) {
        every { keyStore.read() } returns null
        val viewModel = viewModelWith(status = status, body = """{"error":"nope"}""")

        viewModel.state.test {
            assertEquals(OnboardingState.Idle(), awaitItem())

            viewModel.event(OnboardingEvent.OnValidate("cursor-key"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(OnboardingState.Validating("cursor-key"), awaitItem())
            assertEquals(
                OnboardingState.ValidationFailed(
                    keyDraft = "cursor-key",
                    message = expectedMessage,
                ),
                awaitItem(),
            )
        }
        verify(exactly = 0) { keyStore.save(any()) }
    }

    private fun viewModelWith(
        status: HttpStatusCode,
        body: String = ME_RESPONSE_JSON,
    ): OnboardingViewModel =
        viewModelWith(
            MockEngine {
                respond(
                    content = body,
                    status = status,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

    private fun viewModelWith(engine: MockEngine): OnboardingViewModel {
        val factory =
            CursorApiClientFactory { key ->
                CursorApiClient(
                    apiKey = key,
                    baseUrl = BASE_URL,
                    engine = engine,
                )
            }
        return OnboardingViewModel(
            keyStore = keyStore,
            clientFactory = factory,
            messages = messages,
            ioDispatcher = testDispatcher,
        )
    }

    private companion object {
        private const val BASE_URL = "https://api.cursor.test"
        private const val UNAUTHORIZED_MESSAGE = "unauthorized"
        private const val FORBIDDEN_MESSAGE = "forbidden"
        private const val CURSOR_UNAVAILABLE_MESSAGE = "cursor unavailable"
        private const val NETWORK_MESSAGE = "network"
        private const val ME_RESPONSE_JSON = """
            {"apiKeyName":"Personal key","createdAt":"2026-05-11T00:00:00Z","userEmail":"user@example.com"}
        """
        private val ME_RESPONSE =
            MeResponse(
                apiKeyName = "Personal key",
                createdAt = "2026-05-11T00:00:00Z",
                userEmail = "user@example.com",
            )
    }
}
