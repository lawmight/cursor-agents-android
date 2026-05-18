package fr.lawmight.cursoragents.ui.nav

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.lawmight.cursoragents.ui.onboarding.OnboardingScreen
import fr.lawmight.cursoragents.ui.screens.agentdetail.AgentDetailScreen
import fr.lawmight.cursoragents.ui.screens.agentlist.AgentListScreen
import fr.lawmight.cursoragents.ui.screens.launch.LaunchAgentScreen
import fr.lawmight.cursoragents.ui.settings.SettingsScreen

object Routes {
    const val Onboarding = "onboarding"
    const val AgentList = "agents/list"
    const val LaunchAgent = "agents/launch"
    const val CreateAgent = LaunchAgent
    const val ONBOARDING = Onboarding
    const val AGENTS = AgentList
    const val LAUNCH = LaunchAgent
    const val DETAIL = "agents/{id}"
    const val SETTINGS = "settings"

    fun detail(id: String) = "agents/${Uri.encode(id)}"
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
                onCreateAgent = { navController.navigate(Routes.LaunchAgent) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenAgent = { id -> navController.navigate(Routes.detail(id)) },
            )
        }
        composable(Routes.LaunchAgent) {
            LaunchAgentScreen(
                onBack = { navController.popBackStack() },
                onLaunched = {
                    navController.popBackStack(Routes.AgentList, inclusive = false)
                },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) {
            AgentDetailScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
    }
}
