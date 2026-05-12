package fr.lawmight.cursoragents.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.ui.brand.BrandMark
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.PrimaryButton
import fr.lawmight.cursoragents.ui.components.SecondaryButton
import fr.lawmight.cursoragents.ui.components.SecurePromptField
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

data class OnboardingUiState(
    val key: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun OnboardingScreen(onValidated: () -> Unit) {
    var state by remember { mutableStateOf(OnboardingUiState()) }
    val uri = LocalUriHandler.current

    OnboardingContent(
        state = state,
        onKeyChange = { state = state.copy(key = it, errorMessage = null) },
        onValidate = {
            // Wiring to AgentsRepository follows in a CUR-N follow-up; for the
            // preview-driven foundation pass we just gate the navigation on a
            // non-blank key.
            if (state.key.isBlank()) return@OnboardingContent
            state = state.copy(isLoading = true, errorMessage = null)
            onValidated()
        },
        onGetKey = { uri.openUri("https://cursor.com/dashboard?tab=integrations") },
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    onKeyChange: (String) -> Unit,
    onValidate: () -> Unit,
    onGetKey: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.l)
                .padding(top = spacing.xl, bottom = spacing.l),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(spacing.xl))
            BrandMark(size = 96.dp)
            Spacer(Modifier.height(spacing.l))
            Text(
                text = stringResource(R.string.onboarding_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(spacing.xs))
            Text(
                text = stringResource(R.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f),
            )
            Spacer(Modifier.height(spacing.xl))

            SecurePromptField(
                value = state.key,
                onValueChange = onKeyChange,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.onboarding_paste_key),
                placeholder = stringResource(R.string.onboarding_paste_key_hint),
                isError = state.errorMessage != null,
                errorMessage = state.errorMessage,
            )
            Spacer(Modifier.height(spacing.l))

            PrimaryButton(
                text = if (state.isLoading) {
                    stringResource(R.string.onboarding_validating)
                } else {
                    stringResource(R.string.onboarding_validate)
                },
                onClick = onValidate,
                enabled = state.key.isNotBlank(),
                loading = state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(spacing.s))
            SecondaryButton(
                text = stringResource(R.string.onboarding_get_key),
                onClick = onGetKey,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(spacing.xl))
            Box(modifier = Modifier.weight(1f, fill = false))
        }
    }
}

@Preview(name = "Light - empty", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewLight() {
    PreviewSurface(darkTheme = false) {
        OnboardingContent(
            state = OnboardingUiState(),
            onKeyChange = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Light - error", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewLightError() {
    PreviewSurface(darkTheme = false) {
        OnboardingContent(
            state = OnboardingUiState(
                key = "key_xxx",
                errorMessage = "That key was rejected. Double-check it and try again.",
            ),
            onKeyChange = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Light - loading", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewLightLoading() {
    PreviewSurface(darkTheme = false) {
        OnboardingContent(
            state = OnboardingUiState(key = "key_abc123", isLoading = true),
            onKeyChange = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Dark - empty", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewDark() {
    PreviewSurface(darkTheme = true) {
        OnboardingContent(
            state = OnboardingUiState(),
            onKeyChange = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Dark - loading", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewDarkLoading() {
    PreviewSurface(darkTheme = true) {
        OnboardingContent(
            state = OnboardingUiState(key = "key_abc123", isLoading = true),
            onKeyChange = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}
