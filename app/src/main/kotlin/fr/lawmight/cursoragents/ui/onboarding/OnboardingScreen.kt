package fr.lawmight.cursoragents.ui.onboarding

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.data.api.MeResponse
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun OnboardingScreen(
    onValidated: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is OnboardingState.Validated -> ValidatedContent(
            me = currentState.me,
            onContinue = onValidated,
            onUseDifferentKey = {
                viewModel.event(OnboardingEvent.OnUseDifferentKey)
            },
        )
        is OnboardingState.Idle -> KeyEntryContent(
            keyDraft = currentState.keyDraft,
            errorMessage = null,
            isValidating = false,
            onKeyChanged = { viewModel.event(OnboardingEvent.OnKeyChanged(it)) },
            onValidate = { viewModel.event(OnboardingEvent.OnValidate(currentState.keyDraft)) },
        )
        is OnboardingState.Validating -> KeyEntryContent(
            keyDraft = currentState.keyDraft,
            errorMessage = null,
            isValidating = true,
            onKeyChanged = { viewModel.event(OnboardingEvent.OnKeyChanged(it)) },
            onValidate = { viewModel.event(OnboardingEvent.OnValidate(currentState.keyDraft)) },
        )
        is OnboardingState.ValidationFailed -> KeyEntryContent(
            keyDraft = currentState.keyDraft,
            errorMessage = currentState.message,
            isValidating = false,
            onKeyChanged = { viewModel.event(OnboardingEvent.OnKeyChanged(it)) },
            onValidate = { viewModel.event(OnboardingEvent.OnValidate(currentState.keyDraft)) },
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
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var isKeyVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.l),
        verticalArrangement = Arrangement.spacedBy(spacing.m, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {
        OnboardingHeader()
        ApiKeyTextField(
            value = keyDraft,
            onValueChange = onKeyChanged,
            errorMessage = errorMessage,
            isEnabled = !isValidating,
            isKeyVisible = isKeyVisible,
            onVisibilityToggle = { isKeyVisible = !isKeyVisible },
        )
        ValidateKeyButton(
            isEnabled = keyDraft.isNotBlank() && !isValidating,
            isValidating = isValidating,
            onClick = onValidate,
        )
        GetKeyLinkRow(
            onClick = {
                CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(context, Uri.parse(CURSOR_KEY_URL))
            },
        )
    }
}

@Composable
private fun OnboardingHeader() {
    Text(
        text = stringResource(R.string.onboarding_title),
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        text = stringResource(R.string.onboarding_subhead),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
@Suppress("LongParameterList")
private fun ApiKeyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String?,
    isEnabled: Boolean,
    isKeyVisible: Boolean,
    onVisibilityToggle: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.onboarding_paste_key)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = isEnabled,
        isError = errorMessage != null,
        visualTransformation = if (isKeyVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            ApiKeyVisibilityToggle(
                isKeyVisible = isKeyVisible,
                onClick = onVisibilityToggle,
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
        ),
        supportingText = errorMessage?.let { message ->
            {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

@Composable
private fun ApiKeyVisibilityToggle(
    isKeyVisible: Boolean,
    onClick: () -> Unit,
) {
    val icon = if (isKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
    val contentDescription = if (isKeyVisible) {
        stringResource(R.string.onboarding_hide_key)
    } else {
        stringResource(R.string.onboarding_show_key)
    }

    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Composable
private fun ValidateKeyButton(
    isEnabled: Boolean,
    isValidating: Boolean,
    onClick: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (isValidating) {
            CircularProgressIndicator(
                modifier = Modifier.size(spacing.l),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = spacing.xxs,
            )
        } else {
            Text(stringResource(R.string.onboarding_validate))
        }
    }
}

@Composable
private fun GetKeyLinkRow(onClick: () -> Unit) {
    val spacing = LocalSpacing.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.onboarding_get_key_prompt),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.width(spacing.xs))
        TextButton(onClick = onClick) {
            Text(stringResource(R.string.onboarding_get_key))
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.l),
        verticalArrangement = Arrangement.spacedBy(spacing.m, Alignment.CenterVertically),
        horizontalAlignment = CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = stringResource(R.string.onboarding_connected_icon),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(spacing.xl * 2),
        )
        Text(
            text = stringResource(R.string.onboarding_connected_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(spacing.xs))
        StatRow(
            label = stringResource(R.string.onboarding_account_label),
            value = me.userEmail,
        )
        StatRow(
            label = stringResource(R.string.onboarding_key_label),
            value = me.apiKeyName,
        )
        Spacer(Modifier.height(spacing.xs))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.onboarding_continue))
        }
        TextButton(onClick = onUseDifferentKey) {
            Text(stringResource(R.string.onboarding_use_different_key))
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.width(spacing.m))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private const val CURSOR_KEY_URL = "https://cursor.com/dashboard?tab=integrations"
