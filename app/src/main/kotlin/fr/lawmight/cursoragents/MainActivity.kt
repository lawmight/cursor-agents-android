package fr.lawmight.cursoragents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.lawmight.cursoragents.ui.nav.AppNavHost
import fr.lawmight.cursoragents.ui.theme.CursorAgentsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CursorAgentsTheme {
                val nav = rememberNavController()
                AppNavHost(navController = nav)
            }
        }
    }
}
