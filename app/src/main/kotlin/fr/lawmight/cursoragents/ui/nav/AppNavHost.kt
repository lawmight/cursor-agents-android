package fr.lawmight.cursoragents.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.lawmight.cursoragents.ui.agents.AgentListScreen
import fr.lawmight.cursoragents.ui.detail.AgentDetailScreen
import fr.lawmight.cursoragents.ui.launch.LaunchAgentScreen
import fr.lawmight.cursoragents.ui.onboarding.OnboardingScreen
import fr.lawmight.cursoragents.ui.settings.SettingsScreen

object Routes {
    const val Onboarding = "onboarding"
    const val AgentList = "agents"
    const val ONBOARDING = Onboarding
    const val AGENTS = AgentList
    const val LAUNCH = "launch"
    const val DETAIL = "agent/{id}"
    const val SETTINGS = "settings"

    fun detail(id: String) = "agent/$id"
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
                onLaunch = { navController.navigate(Routes.LAUNCH) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onOpen = { id -> navController.navigate(Routes.detail(id)) },
            )
        }
        composable(Routes.LAUNCH) { LaunchAgentScreen(onLaunched = { navController.popBackStack() }) }
        composable(Routes.DETAIL) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            AgentDetailScreen(agentId = id, onClose = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}
