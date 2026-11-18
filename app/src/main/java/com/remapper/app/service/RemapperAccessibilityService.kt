package com.remapper.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.remapper.app.data.PreferencesManager
import com.remapper.app.data.TriggerType

class RemapperAccessibilityService : AccessibilityService() {

    private lateinit var actionExecutor: ActionExecutor
    private lateinit var keyDetector: KeyCombinationDetector
    private lateinit var prefsManager: PreferencesManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true

        prefsManager = PreferencesManager.getInstance(this)
        actionExecutor = ActionExecutor(this)
        keyDetector = KeyCombinationDetector(this) { trigger ->
            onTriggerFired(trigger)
        }

        // Configure accessibility info
        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = flags or
                    AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 50
        }
        serviceInfo = info
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        return keyDetector.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No window content scraping needed, keeping memory footprint ultra-lightweight
    }

    override fun onInterrupt() {
        // Handle accessibility interruption
    }

    private fun onTriggerFired(trigger: TriggerType) {
        if (!prefsManager.isTriggerEnabled(trigger)) return

        val action = prefsManager.getActionForTrigger(trigger) ?: return
        actionExecutor.execute(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        if (::keyDetector.isInitialized) {
            keyDetector.onDestroy()
        }
    }

    companion object {
        @Volatile
        var isRunning: Boolean = false
            private set

        /**
         * Checks whether RemapperAccessibilityService is enabled in Android's Accessibility settings.
         */
        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedComponentName = ComponentName(context, RemapperAccessibilityService::class.java).flattenToString()
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)

            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                if (componentNameString.equals(expectedComponentName, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }
}
