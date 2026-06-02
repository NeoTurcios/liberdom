package openfind.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import openfind.ai.ui.navigation.NavGraph
import openfind.ai.ui.theme.OpenFindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            OpenFindTheme {
                NavGraph()
            }
        }
    }
}
