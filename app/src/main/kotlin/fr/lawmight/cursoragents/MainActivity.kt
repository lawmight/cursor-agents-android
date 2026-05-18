package fr.lawmight.cursoragents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import fr.lawmight.cursoragents.data.auth.EncryptedKeyStore
import fr.lawmight.cursoragents.ui.nav.AppNavHost
import fr.lawmight.cursoragents.ui.theme.CursorAgentsTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var keyStore: EncryptedKeyStore

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CursorAgentsTheme {
                val nav = rememberNavController()
                val hasSavedKey by produceState<Boolean?>(initialValue = null) {
                    value = keyStore.get() != null
                }
                hasSavedKey?.let { resolved ->
                    AppNavHost(navController = nav, hasSavedKey = resolved)
                }
            }
        }
    }
}
