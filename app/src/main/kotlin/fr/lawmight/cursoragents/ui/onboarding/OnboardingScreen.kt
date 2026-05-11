package fr.lawmight.cursoragents.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.data.api.MeResponse
import fr.lawmight.cursoragents.ui.brand.BrandMark
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.PrimaryButton
import fr.lawmight.cursoragents.ui.components.SecondaryButton
import fr.lawmight.cursoragents.ui.components.SecurePromptField
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun OnboardingScreen(
    onValidated: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val uri = LocalUriHandler.current

    when (val currentState = state) {
        is OnboardingState.Validated ->
            ValidatedContent(
                me = currentState.me,
                onContinue = onValidated,
                onUseDifferentKey = { viewModel.event(OnboardingEvent.OnUseDifferentKey) },
            )

        is OnboardingState.Idle ->
            KeyEntryContent(
                keyDraft = currentState.keyDraft,
                errorMessage = null,
                isValidating = false,
                onKeyChanged = { viewModel.event(OnboardingEvent.OnKeyChanged(it)) },
                onValidate = { viewModel.event(OnboardingEvent.OnValidate(currentState.keyDraft)) },
                onGetKey = { uri.openUri(CURSOR_KEY_URL) },
            )

        is OnboardingState.Validating ->
            KeyEntryContent(
                keyDraft = currentState.keyDraft,
                errorMessage = null,
                isValidating = true,
                onKeyChanged = { viewModel.event(OnboardingEvent.OnKeyChanged(it)) },
                onValidate = { viewModel.event(OnboardingEvent.OnValidate(currentState.keyDraft)) },
                onGetKey = { uri.openUri(CURSOR_KEY_URL) },
            )

        is OnboardingState.ValidationFailed ->
            KeyEntryContent(
                keyDraft = currentState.keyDraft,
                errorMessage = currentState.message,
                isValidating = false,
                onKeyChanged = { viewModel.event(OnboardingEvent.OnKeyChanged(it)) },
                onValidate = { viewModel.event(OnboardingEvent.OnValidate(currentState.keyDraft)) },
                onGetKey = { uri.openUri(CURSOR_KEY_URL) },
            )
    }
}

@Composable
private fun KeyEntryContent(
    keyDraft: String,
    errorMessage: String?,
    isValidating: Boolean,
    onKeyChanged: (String) -> Unit,
    onValidate: () -> Unit,
    onGetKey: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
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
                value = keyDraft,
                onValueChange = onKeyChanged,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.onboarding_paste_key),
                placeholder = stringResource(R.string.onboarding_paste_key_hint),
                isError = errorMessage != null,
                errorMessage = errorMessage,
            )
            Spacer(Modifier.height(spacing.l))

            PrimaryButton(
                text =
                    if (isValidating) {
                        stringResource(R.string.onboarding_validating)
                    } else {
                        stringResource(R.string.onboarding_validate)
                    },
                onClick = onValidate,
                enabled = keyDraft.isNotBlank() && !isValidating,
                loading = isValidating,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(spacing.s))
            SecondaryButton(
                text = stringResource(R.string.onboarding_get_key),
                onClick = onGetKey,
                enabled = !isValidating,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(spacing.xl))
            Box(modifier = Modifier.weight(1f, fill = false))
        }
    }
}

@Composable
private fun ValidatedContent(
    me: MeResponse,
    onContinue: () -> Unit,
    onUseDifferentKey: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
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
                text = stringResource(R.string.onboarding_connected_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(spacing.l))
            StatRow(
                label = stringResource(R.string.onboarding_account_label),
                value = me.userEmail,
            )
            Spacer(Modifier.height(spacing.xs))
            StatRow(
                label = stringResource(R.string.onboarding_key_label),
                value = me.apiKeyName,
            )
            Spacer(Modifier.height(spacing.l))
            PrimaryButton(
                text = stringResource(R.string.onboarding_continue),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(spacing.s))
            SecondaryButton(
                text = stringResource(R.string.onboarding_use_different_key),
                onClick = onUseDifferentKey,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(spacing.xl))
            Box(modifier = Modifier.weight(1f, fill = false))
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(spacing.m))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "Light - empty", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewLight() {
    PreviewSurface(darkTheme = false) {
        KeyEntryContent(
            keyDraft = "",
            errorMessage = null,
            isValidating = false,
            onKeyChanged = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Light - error", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewLightError() {
    PreviewSurface(darkTheme = false) {
        KeyEntryContent(
            keyDraft = "key_xxx",
            errorMessage = "That key was rejected. Double-check it and try again.",
            isValidating = false,
            onKeyChanged = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Light - loading", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewLightLoading() {
    PreviewSurface(darkTheme = false) {
        KeyEntryContent(
            keyDraft = "key_abc123",
            errorMessage = null,
            isValidating = true,
            onKeyChanged = {},
            onValidate = {},
            onGetKey = {},
        )
    }
}

@Preview(name = "Dark - connected", widthDp = 360, heightDp = 720)
@Composable
private fun OnboardingPreviewDarkConnected() {
    PreviewSurface(darkTheme = true) {
        ValidatedContent(
            me =
                MeResponse(
                    apiKeyName = "Personal key",
                    createdAt = "2026-05-11T00:00:00Z",
                    userEmail = "user@example.com",
                ),
            onContinue = {},
            onUseDifferentKey = {},
        )
    }
}

private const val CURSOR_KEY_URL = "https://cursor.com/dashboard?tab=integrations"
