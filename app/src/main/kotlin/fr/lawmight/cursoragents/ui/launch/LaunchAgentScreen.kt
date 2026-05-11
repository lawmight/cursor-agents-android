package fr.lawmight.cursoragents.ui.launch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.R

@Composable
fun LaunchAgentScreen(onLaunched: () -> Unit) {
    var repo by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("main") }
    var prompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.launch_title))
        OutlinedTextField(value = repo, onValueChange = { repo = it }, label = { Text(stringResource(R.string.launch_repo)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = branch, onValueChange = { branch = it }, label = { Text(stringResource(R.string.launch_branch)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text(stringResource(R.string.launch_prompt)) }, modifier = Modifier.fillMaxWidth(), minLines = 4)
        Button(onClick = onLaunched, enabled = repo.isNotBlank() && prompt.isNotBlank()) {
            Text(stringResource(R.string.launch_submit))
        }
    }
}
