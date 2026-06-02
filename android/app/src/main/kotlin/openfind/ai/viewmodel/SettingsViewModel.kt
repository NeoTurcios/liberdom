package openfind.ai.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import openfind.ai.data.ai.DomainAiEvaluator
import openfind.ai.data.repository.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SettingsState(
    val isAiEnabled: Boolean = false,
    val language: String = "es",
    val notificationInterval: Int = 24,
    val notificationsEnabled: Boolean = false,
    val deviceModel: String = "",
    val androidVersion: String = "",
    val aiLoading: Boolean = false,
    val aiLoadError: String? = null,
    val aiLoadSuccess: Boolean = false,
    val aiProgress: Float = 0f
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val context: Context
) : ViewModel(), KoinComponent {

    private val domainAiEvaluator: DomainAiEvaluator by inject()

    private val _state = MutableStateFlow(
        SettingsState(
            isAiEnabled = settingsRepository.isAiEnabled,
            language = settingsRepository.language,
            notificationInterval = settingsRepository.notificationIntervalHours,
            notificationsEnabled = settingsRepository.notificationsEnabled,
            deviceModel = android.os.Build.MODEL,
            androidVersion = "Android ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})"
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onToggleAi(enabled: Boolean) {
        if (enabled) {
            _state.update { it.copy(aiLoading = true, aiProgress = 0f, aiLoadError = null, aiLoadSuccess = false) }
            viewModelScope.launch {
                // Simulate model download progress beautifully over ~3 seconds
                for (p in 1..100) {
                    kotlinx.coroutines.delay(30)
                    _state.update { it.copy(aiProgress = p / 100f) }
                }

                val success = withContext(Dispatchers.IO) {
                    domainAiEvaluator.tryEnableAi()
                }

                if (success) {
                    settingsRepository.setAiEnabled(true)
                    _state.update { it.copy(isAiEnabled = true, aiLoading = false, aiLoadSuccess = true) }
                } else {
                    settingsRepository.setAiEnabled(false)
                    _state.update {
                        it.copy(
                            isAiEnabled = false,
                            aiLoading = false,
                            aiLoadError = "Failed to load model"
                        )
                    }
                }
            }
        } else {
            domainAiEvaluator.disableAi()
            settingsRepository.setAiEnabled(false)
            _state.update { it.copy(isAiEnabled = false, aiLoading = false, aiLoadError = null, aiProgress = 0f) }
        }
    }

    fun onChangeLanguage(language: String) {
        settingsRepository.setLanguage(language)
        _state.update { it.copy(language = language) }
    }

    fun onChangeNotificationInterval(hours: Int) {
        settingsRepository.setNotificationInterval(hours)
        _state.update { it.copy(notificationInterval = hours) }
    }

    fun onToggleNotifications(enabled: Boolean) {
        settingsRepository.setNotificationsEnabled(enabled)
        _state.update { it.copy(notificationsEnabled = enabled) }
    }
}
