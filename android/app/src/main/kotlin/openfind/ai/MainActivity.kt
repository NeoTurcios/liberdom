package openfind.ai

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import openfind.ai.data.repository.SettingsRepository
import openfind.ai.ui.navigation.NavGraph
import openfind.ai.ui.theme.OpenFindTheme
import openfind.ai.ui.utils.LocalLanguage
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()
    private val languageState = mutableStateOf("es")

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "language") {
            val newLang = settingsRepository.language
            Log.d("MainActivity", "Language preference changed to: $newLang")
            languageState.value = newLang
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            Log.w("MainActivity", "enableEdgeToEdge failed: ${e.message}")
        }

        // Initialize state with stored language (default "es")
        languageState.value = settingsRepository.language

        // Listen for preference modifications
        val prefs = getSharedPreferences("openfind_settings", Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        try {
            setContent {
                OpenFindTheme {
                    CompositionLocalProvider(LocalLanguage provides languageState.value) {
                        NavGraph()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "setContent crashed: ${e.message}", e)
            throw e
        }
    }

    override fun onDestroy() {
        try {
            val prefs = getSharedPreferences("openfind_settings", Context.MODE_PRIVATE)
            prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        } catch (e: Exception) {
            Log.w("MainActivity", "Failed to unregister preferences listener: ${e.message}")
        }
        super.onDestroy()
    }
}
