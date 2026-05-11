package fr.lawmight.cursoragents.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.lawmight.cursoragents.data.api.CursorApiException
import fr.lawmight.cursoragents.data.api.MeResponse
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.di.CursorApiClientFactory
import fr.lawmight.cursoragents.di.IoDispatcher
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val keyStore: EncryptedKeyStore,
    private val clientFactory: CursorApiClientFactory,
    private val messages: OnboardingErrorMessages,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Idle())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private var validationJob: Job? = null

    init {
        validateSavedKey()
    }

    fun event(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.OnKeyChanged -> onKeyChanged(event.keyDraft)
            is OnboardingEvent.OnValidate -> validateUserKey(event.key)
            OnboardingEvent.OnUseDifferentKey -> useDifferentKey()
        }
    }

    private fun validateSavedKey() {
        val savedKey = keyStore.read() ?: return
        validationJob?.cancel()
        validationJob = viewModelScope.launch(ioDispatcher) {
            _state.value = OnboardingState.Validating(savedKey)
            when (val result = validateKey(savedKey)) {
                is ValidationResult.Success -> _state.value = OnboardingState.Validated(result.me)
                is ValidationResult.Failure -> _state.value = OnboardingState.Idle(savedKey)
            }
        }
    }

    private fun onKeyChanged(keyDraft: String) {
        val current = _state.value
        if (current is OnboardingState.Validating || current is OnboardingState.Validated) {
            return
        }
        _state.value = OnboardingState.Idle(keyDraft)
    }

    private fun validateUserKey(key: String) {
        val trimmedKey = key.trim()
        if (trimmedKey.isBlank()) return

        validationJob?.cancel()
        validationJob = viewModelScope.launch(ioDispatcher) {
            _state.value = OnboardingState.Validating(trimmedKey)
            when (val result = validateKey(trimmedKey)) {
                is ValidationResult.Success -> {
                    keyStore.save(trimmedKey)
                    _state.value = OnboardingState.Validated(result.me)
                }
                is ValidationResult.Failure -> {
                    _state.value = OnboardingState.ValidationFailed(
                        keyDraft = trimmedKey,
                        message = result.message,
                    )
                }
            }
        }
    }

    private fun useDifferentKey() {
        validationJob?.cancel()
        keyStore.remove(keyStore.activeAlias() ?: EncryptedKeyStore.DEFAULT_ALIAS)
        _state.value = OnboardingState.Idle()
    }

    private suspend fun validateKey(key: String): ValidationResult {
        val client = clientFactory.create(key.trim())
        return try {
            ValidationResult.Success(client.me())
        } catch (exception: CursorApiException) {
            ValidationResult.Failure(exception.toMessage())
        } catch (exception: IOException) {
            ValidationResult.Failure(messages.network())
        } finally {
            client.close()
        }
    }

    private fun CursorApiException.toMessage(): String =
        when (this) {
            CursorApiException.Unauthorized -> messages.unauthorized()
            CursorApiException.Forbidden -> messages.forbidden()
            CursorApiException.NotFound,
            CursorApiException.RateLimited,
            is CursorApiException.Unexpected,
            -> messages.cursorUnavailable()
        }

    private sealed interface ValidationResult {
        data class Success(val me: MeResponse) : ValidationResult

        data class Failure(val message: String) : ValidationResult
    }
}

sealed interface OnboardingEvent {
    data class OnKeyChanged(val keyDraft: String) : OnboardingEvent

    data class OnValidate(val key: String) : OnboardingEvent

    data object OnUseDifferentKey : OnboardingEvent
}
