package fr.lawmight.cursoragents.ui.onboarding

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.lawmight.cursoragents.R
import javax.inject.Inject

class OnboardingErrorMessages
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun unauthorized(): String = context.getString(R.string.onboarding_error_unauthorized)

        fun forbidden(): String = context.getString(R.string.onboarding_error_forbidden)

        fun cursorUnavailable(): String = context.getString(R.string.onboarding_error_cursor_unavailable)

        fun network(): String = context.getString(R.string.onboarding_error_network)
    }
