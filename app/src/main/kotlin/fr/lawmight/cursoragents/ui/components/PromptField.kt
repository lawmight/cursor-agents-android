package fr.lawmight.cursoragents.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

@Composable
fun PromptField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    label: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    minLines: Int = 1,
    maxLines: Int = 8,
    showCharCount: Boolean = false,
    maxChars: Int? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (maxChars == null || input.length <= maxChars) onValueChange(input)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            placeholder = placeholder?.let {
                {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            label = label?.let { { Text(it) } },
            minLines = minLines,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyLarge,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
        )
        if (errorMessage != null && isError) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = spacing.s, top = spacing.xxs),
            )
        }
        if (showCharCount && maxChars != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = spacing.xxs),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${value.length} / $maxChars",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Preset for the onboarding API key field: secure-input with a show/hide eye.
 */
@Composable
fun SecurePromptField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var visible by remember { mutableStateOf(false) }
    PromptField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.None),
        trailingIcon = {
            GhostIconButton(
                icon = if (visible) {
                    androidx.compose.material.icons.Icons.Default.Visibility
                } else {
                    androidx.compose.material.icons.Icons.Default.VisibilityOff
                },
                onClick = { visible = !visible },
                contentDescription = if (visible) "Hide key" else "Show key",
            )
        },
    )
}

@Preview(name = "Light", widthDp = 360)
@Composable
private fun PromptFieldPreviewLight() {
    PreviewSurface(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PromptField(
                value = "Refactor the navigation host to use type-safe routes.",
                onValueChange = {},
                label = "Prompt",
                placeholder = "What should the agent do?",
                minLines = 3,
                showCharCount = true,
                maxChars = 4000,
                trailingIcon = {
                    GhostIconButton(Icons.Default.Mic, onClick = {}, contentDescription = "Voice input")
                },
            )
            PromptField(
                value = "",
                onValueChange = {},
                label = "Branch",
                placeholder = "main",
                isError = true,
                errorMessage = "Branch is required.",
            )
            SecurePromptField(value = "key_xxx", onValueChange = {}, label = "Cursor API key")
        }
    }
}

@Preview(name = "Dark", widthDp = 360)
@Composable
private fun PromptFieldPreviewDark() {
    PreviewSurface(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PromptField(
                value = "Refactor the navigation host to use type-safe routes.",
                onValueChange = {},
                label = "Prompt",
                placeholder = "What should the agent do?",
                minLines = 3,
                showCharCount = true,
                maxChars = 4000,
                trailingIcon = {
                    GhostIconButton(Icons.Default.Mic, onClick = {}, contentDescription = "Voice input")
                },
            )
            PromptField(
                value = "",
                onValueChange = {},
                label = "Branch",
                placeholder = "main",
                isError = true,
                errorMessage = "Branch is required.",
            )
            SecurePromptField(value = "key_xxx", onValueChange = {}, label = "Cursor API key")
        }
    }
}
