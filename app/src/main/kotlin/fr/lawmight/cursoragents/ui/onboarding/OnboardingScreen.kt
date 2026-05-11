package fr.lawmight.cursoragents.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.R

@Composable
fun OnboardingScreen(onValidated: () -> Unit) {
    var key by remember { mutableStateOf("") }
    val uri = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(stringResource(R.string.onboarding_title))
        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text(stringResource(R.string.onboarding_paste_key)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = onValidated, enabled = key.isNotBlank()) {
            Text(stringResource(R.string.onboarding_validate))
        }
        TextButton(onClick = { uri.openUri("https://cursor.com/dashboard?tab=integrations") }) {
            Text(stringResource(R.string.onboarding_get_key))
        }
    }
}
