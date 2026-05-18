package fr.lawmight.cursoragents.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.lawmight.cursoragents.R
import fr.lawmight.cursoragents.ui.onboarding.OnboardingScreen
import fr.lawmight.cursoragents.ui.screens.agentlist.AgentListScreen
import fr.lawmight.cursoragents.ui.settings.SettingsScreen
import fr.lawmight.cursoragents.ui.theme.LocalSpacing

object Routes {
    const val Onboarding = "onboarding"
    const val AgentList = "agents/list"
    const val CreateAgent = "agents/create"
    const val ONBOARDING = Onboarding
    const val AGENTS = AgentList
    const val LAUNCH = CreateAgent
    const val DETAIL = "agents/{id}"
    const val SETTINGS = "settings"

    fun detail(id: String) = "agents/$id"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    hasSavedKey: Boolean,
) {
    val startDestination = if (hasSavedKey) Routes.AgentList else Routes.Onboarding

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Onboarding) {
            OnboardingScreen(
                onValidated = {
                    navController.navigate(Routes.AgentList) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.AgentList) {
            AgentListScreen(
                onCreateAgent = { navController.navigate(Routes.CreateAgent) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenAgent = { id -> navController.navigate(Routes.detail(id)) },
            )
        }
        composable(Routes.CreateAgent) { PlaceholderScreen(text = stringResource(R.string.agents_create_placeholder)) }
        composable(Routes.DETAIL) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            PlaceholderScreen(text = stringResource(R.string.detail_placeholder, id))
        }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    val spacing = LocalSpacing.current
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(spacing.l),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
