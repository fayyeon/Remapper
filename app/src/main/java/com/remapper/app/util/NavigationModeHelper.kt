package com.remapper.app.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.KeyCharacterMap
import android.view.KeyEvent

object NavigationModeHelper {

    /**
     * Determines whether the device is currently configured to use 3-button navigation
     * or gesture navigation.
     *
     * Returns true if 3-button (or 2-button) navigation is used, false if full gestural navigation is active.
     */
    fun is3ButtonNavigation(context: Context): Boolean {
        // Pre-Android 10 devices always use hardware or on-screen button navigation
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true
        }

        val cr = context.contentResolver

        // 1. Android Standard Secure setting "navigation_mode"
        // 0 = 3-button, 1 = 2-button, 2 = Gestural
        try {
            val navMode = Settings.Secure.getInt(cr, "navigation_mode", -1)
            if (navMode == 2) return false
            if (navMode == 0 || navMode == 1) return true
        } catch (_: Exception) {}

        // 2. Xiaomi / MIUI / HyperOS
        // force_fsg_nav_bar: 1 = Full screen gestures, 0 = Buttons
        try {
            val fsg = Settings.Global.getInt(cr, "force_fsg_nav_bar", -1)
            if (fsg == 1) return false
            if (fsg == 0) return true
        } catch (_: Exception) {}

        // 3. Samsung OneUI
        // navigation_bar_gesture_while_hidden: 1 = Gestures, 0 = Buttons
        try {
            val gesture = Settings.Global.getInt(cr, "navigation_bar_gesture_while_hidden", -1)
            if (gesture == 1) return false
            if (gesture == 0) return true
        } catch (_: Exception) {}

        // 4. Android internal configuration overlay
        try {
            val resId = context.resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
            if (resId > 0) {
                val mode = context.resources.getInteger(resId)
                if (mode == 2) return false
                if (mode == 0 || mode == 1) return true
            }
        } catch (_: Exception) {}

        // 5. Hardware keys check (e.g. devices with physical back/home keys)
        val hasPhysicalHome = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
        val hasPhysicalBack = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        if (hasPhysicalHome && hasPhysicalBack) {
            return true
        }

        // Default to true so button users aren't locked out on unrecognized skins
        return true
    }
}
