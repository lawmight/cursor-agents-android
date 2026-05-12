package fr.lawmight.cursoragents.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.ui.components.GhostIconButton
import fr.lawmight.cursoragents.ui.components.ListItem
import fr.lawmight.cursoragents.ui.components.PreviewSurface
import fr.lawmight.cursoragents.ui.components.SectionHeader
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

enum class ThemeChoice { System, Light, Dark }

data class SettingsUiState(
    val activeKeyAlias: String = "Personal",
    val activeKeyMasked: String = "key_********",
    val theme: ThemeChoice = ThemeChoice.System,
    val defaultModel: String = "claude-sonnet-4.5",
    val versionName: String = "0.1.0",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var state by remember { mutableStateOf(SettingsUiState()) }
    val uri = LocalUriHandler.current
    SettingsContent(
        state = state,
        onBack = onBack,
        onApiKeys = {},
        onThemeChange = { state = state.copy(theme = it) },
        onDefaultModel = {},
        onAboutRepo = { uri.openUri("https://github.com/lawmight/cursor-agents-android") },
        onAboutLicense = { uri.openUri("https://github.com/lawmight/cursor-agents-android/blob/main/LICENSE") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onApiKeys: () -> Unit,
    onThemeChange: (ThemeChoice) -> Unit,
    onDefaultModel: () -> Unit,
    onAboutRepo: () -> Unit,
    onAboutLicense: () -> Unit,
) {
    val spacing = LocalSpacing.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    GhostIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBack,
                        contentDescription = stringResource(R.string.settings_back),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(bottom = spacing.l),
        ) {
            item { SectionHeader(text = stringResource(R.string.settings_api_keys)) }
            item {
                ListItem(
                    title = "${state.activeKeyAlias} · ${state.activeKeyMasked}",
                    subtitle = stringResource(R.string.settings_api_keys_subtitle),
                    leading = {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = onApiKeys,
                )
            }

            item { SectionHeader(text = stringResource(R.string.settings_theme)) }
            ThemeChoice.entries.forEach { choice ->
                item {
                    ThemeRow(
                        choice = choice,
                        selected = state.theme == choice,
                        onSelect = { onThemeChange(choice) },
                    )
                }
            }

            item { SectionHeader(text = stringResource(R.string.settings_default_model)) }
            item {
                ListItem(
                    title = state.defaultModel,
                    subtitle = stringResource(R.string.settings_default_model_subtitle),
                    leading = {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = onDefaultModel,
                )
            }

            item { SectionHeader(text = stringResource(R.string.settings_about)) }
            item {
                ListItem(
                    title = stringResource(R.string.settings_about_repo),
                    subtitle = stringResource(R.string.settings_version, state.versionName),
                    leading = {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = onAboutRepo,
                    showDivider = true,
                )
            }
            item {
                ListItem(
                    title = stringResource(R.string.settings_about_license),
                    leading = {
                        Icon(
                            Icons.Default.Article,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                    trailing = {
                        Icon(
                            Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = onAboutLicense,
                )
            }
        }
    }
}

@Composable
private fun ThemeRow(choice: ThemeChoice, selected: Boolean, onSelect: () -> Unit) {
    val spacing = LocalSpacing.current
    val label = stringResource(
        when (choice) {
            ThemeChoice.System -> R.string.settings_theme_system
            ThemeChoice.Light -> R.string.settings_theme_light
            ThemeChoice.Dark -> R.string.settings_theme_dark
        },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = spacing.m, vertical = spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.DarkMode,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = spacing.m),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light", widthDp = 360, heightDp = 720)
@Composable
private fun SettingsPreviewLight() {
    PreviewSurface(darkTheme = false) {
        SettingsContent(
            state = SettingsUiState(),
            onBack = {}, onApiKeys = {}, onThemeChange = {}, onDefaultModel = {},
            onAboutRepo = {}, onAboutLicense = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Dark", widthDp = 360, heightDp = 720)
@Composable
private fun SettingsPreviewDark() {
    PreviewSurface(darkTheme = true) {
        SettingsContent(
            state = SettingsUiState(theme = ThemeChoice.Dark),
            onBack = {}, onApiKeys = {}, onThemeChange = {}, onDefaultModel = {},
            onAboutRepo = {}, onAboutLicense = {},
        )
    }
}
