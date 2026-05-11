package fr.lawmight.cursoragents.ui.onboarding

import fr.lawmight.cursoragents.data.api.MeResponse

sealed interface OnboardingState {
    data class Idle(val keyDraft: String = "") : OnboardingState

    data class Validating(val keyDraft: String) : OnboardingState

    data class ValidationFailed(
        val keyDraft: String,
        val message: String,
    ) : OnboardingState

    data class Validated(val me: MeResponse) : OnboardingState
}
