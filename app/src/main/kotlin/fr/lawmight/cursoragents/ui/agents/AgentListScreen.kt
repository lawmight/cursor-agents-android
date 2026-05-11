package fr.lawmight.cursoragents.ui.agents

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.lawmight.cursoragents.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentListScreen(
    onLaunch: () -> Unit,
    onSettings: () -> Unit,
    onOpen: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.agents_title)) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onLaunch) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.agents_new))
            }
        },
    ) { pad ->
        Text(
            text = stringResource(R.string.agents_empty),
            modifier = Modifier.fillMaxSize().padding(pad).padding(24.dp),
        )
    }
}
