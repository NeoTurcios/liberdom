package openfind.ai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import openfind.ai.ui.navigation.NavGraph
import openfind.ai.ui.theme.OpenFindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.w("MainActivity", "enableEdgeToEdge failed: ${e.message}")
        }

        try {
            setContent {
                OpenFindTheme {
                    NavGraph()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "setContent crashed: ${e.message}", e)
            throw e
        }
    }
}
