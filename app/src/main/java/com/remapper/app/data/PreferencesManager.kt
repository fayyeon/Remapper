package com.remapper.app.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getActionForTrigger(trigger: TriggerType): RemapAction? {
        val raw = prefs.getString(KEY_ACTION_PREFIX + trigger.key, null)
        return RemapAction.fromJsonString(raw)
    }

    fun setActionForTrigger(trigger: TriggerType, action: RemapAction?) {
        prefs.edit().apply {
            if (action != null) {
                putString(KEY_ACTION_PREFIX + trigger.key, action.toJsonString())
            } else {
                remove(KEY_ACTION_PREFIX + trigger.key)
            }
            apply()
        }
    }

    fun isTriggerEnabled(trigger: TriggerType): Boolean {
        return prefs.getBoolean(KEY_ENABLED_PREFIX + trigger.key, true)
    }

    fun setTriggerEnabled(trigger: TriggerType, enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED_PREFIX + trigger.key, enabled).apply()
    }

    fun isHapticEnabled(): Boolean {
        return prefs.getBoolean(KEY_HAPTIC, true)
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
    }

    companion object {
        private const val PREF_NAME = "remapper_prefs"
        private const val KEY_ACTION_PREFIX = "action_"
        private const val KEY_ENABLED_PREFIX = "enabled_"
        private const val KEY_HAPTIC = "haptic_feedback"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
