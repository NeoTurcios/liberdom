package openfind.ai.data.repository

import android.content.Context

class SettingsRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("openfind_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AI_ENABLED = "ai_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATION_INTERVAL = "notification_interval"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    val isAiEnabled: Boolean
        get() = prefs.getBoolean(KEY_AI_ENABLED, false)

    fun setAiEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AI_ENABLED, enabled).apply()
    }

    val language: String
        get() = prefs.getString(KEY_LANGUAGE, "es") ?: "es"

    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    val notificationIntervalHours: Int
        get() = prefs.getInt(KEY_NOTIFICATION_INTERVAL, 24)

    fun setNotificationInterval(hours: Int) {
        prefs.edit().putInt(KEY_NOTIFICATION_INTERVAL, hours).apply()
    }

    val notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }
}
