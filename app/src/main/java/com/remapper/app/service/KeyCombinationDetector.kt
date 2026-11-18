package com.remapper.app.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.KeyEvent
import com.remapper.app.data.TriggerType
import com.remapper.app.util.NavigationModeHelper

/**
 * State machine for detecting hardware button combinations:
 * - 3-Button Navigation: Hold Home (overrides Assistant), Hold Recents, Hold Back
 * - Volume & Power: Vol Up + Down, Vol Up + Power (Single, Hold, Double Press)
 */
class KeyCombinationDetector(
    private val service: AccessibilityService,
    private val isTriggerActive: (TriggerType) -> Boolean,
    private val onTriggerDetected: (TriggerType) -> Unit
) {

    private val context: Context get() = service
    private val handler = Handler(Looper.getMainLooper())

    // Key states
    private var isVolUpPressed = false
    private var isVolDownPressed = false
    private var isPowerPressed = false

    private var volUpPressTime = 0L
    private var volDownPressTime = 0L
    private var powerPressTime = 0L

    // Hold Navigation Keys tracking
    private var isHomeHoldTriggered = false
    private var isRecentsHoldTriggered = false
    private var isBackHoldTriggered = false

    private val homeHoldRunnable = Runnable {
        isHomeHoldTriggered = true
        onTriggerDetected(TriggerType.HOLD_HOME)
    }

    private val recentsHoldRunnable = Runnable {
        isRecentsHoldTriggered = true
        onTriggerDetected(TriggerType.HOLD_RECENTS)
    }

    private val backHoldRunnable = Runnable {
        isBackHoldTriggered = true
        onTriggerDetected(TriggerType.HOLD_BACK)
    }

    // Volume Up + Down combo tracking
    private var isVolUpDownTriggered = false

    // Volume Up + Power combo tracking
    private var isVolPowerBothDown = false
    private var isVolPowerHoldTriggered = false
    private var lastVolPowerComboReleaseTime = 0L

    private val volPowerHoldRunnable = Runnable {
        if (isVolPowerBothDown) {
            isVolPowerHoldTriggered = true
            onTriggerDetected(TriggerType.VOLUME_UP_PLUS_POWER_HOLD)
        }
    }

    private val volPowerSinglePressRunnable = Runnable {
        onTriggerDetected(TriggerType.VOLUME_UP_PLUS_POWER_SINGLE)
    }

    // Screen receiver to assist power key detection
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val now = SystemClock.uptimeMillis()
            if (isVolUpPressed && (now - volUpPressTime) < 800) {
                handleVolPowerComboDown(now)
                handler.postDelayed({
                    handleVolPowerComboUp(now)
                }, 100)
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        context.registerReceiver(screenReceiver, filter)
    }

    fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        try {
            context.unregisterReceiver(screenReceiver)
        } catch (_: Exception) {}
    }

    /**
     * Intercepts key events from AccessibilityService.
     */
    fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val action = event.action
        val now = SystemClock.uptimeMillis()

        // 1. Navigation Button Holds (Only enabled on 3-button navigation)
        if (NavigationModeHelper.is3ButtonNavigation(context)) {
            when (keyCode) {
                KeyEvent.KEYCODE_HOME -> {
                    if (!isTriggerActive(TriggerType.HOLD_HOME)) return false

                    if (action == KeyEvent.ACTION_DOWN) {
                        isHomeHoldTriggered = false
                        handler.removeCallbacks(homeHoldRunnable)
                        handler.postDelayed(homeHoldRunnable, HOLD_NAV_BUTTON_TIMEOUT_MS)
                        return true
                    } else if (action == KeyEvent.ACTION_UP) {
                        handler.removeCallbacks(homeHoldRunnable)
                        if (isHomeHoldTriggered) {
                            isHomeHoldTriggered = false
                            return true
                        } else {
                            // Released before hold duration: perform standard Home action
                            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
                            return true
                        }
                    }
                    return false
                }

                KeyEvent.KEYCODE_APP_SWITCH -> {
                    if (!isTriggerActive(TriggerType.HOLD_RECENTS)) return false

                    if (action == KeyEvent.ACTION_DOWN) {
                        isRecentsHoldTriggered = false
                        handler.removeCallbacks(recentsHoldRunnable)
                        handler.postDelayed(recentsHoldRunnable, HOLD_NAV_BUTTON_TIMEOUT_MS)
                        return true
                    } else if (action == KeyEvent.ACTION_UP) {
                        handler.removeCallbacks(recentsHoldRunnable)
                        if (isRecentsHoldTriggered) {
                            isRecentsHoldTriggered = false
                            return true
                        } else {
                            // Released before hold duration: perform standard Recents action
                            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
                            return true
                        }
                    }
                    return false
                }

                KeyEvent.KEYCODE_BACK -> {
                    if (!isTriggerActive(TriggerType.HOLD_BACK)) return false

                    if (action == KeyEvent.ACTION_DOWN) {
                        isBackHoldTriggered = false
                        handler.removeCallbacks(backHoldRunnable)
                        handler.postDelayed(backHoldRunnable, HOLD_NAV_BUTTON_TIMEOUT_MS)
                        return true
                    } else if (action == KeyEvent.ACTION_UP) {
                        handler.removeCallbacks(backHoldRunnable)
                        if (isBackHoldTriggered) {
                            isBackHoldTriggered = false
                            return true
                        } else {
                            // Released before hold duration: perform standard Back action
                            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                            return true
                        }
                    }
                    return false
                }
            }
        }

        // 2. Volume & Power Combinations
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (action == KeyEvent.ACTION_DOWN) {
                    isVolUpPressed = true
                    volUpPressTime = now
                    return checkCombosOnDown(now)
                } else if (action == KeyEvent.ACTION_UP) {
                    isVolUpPressed = false
                    return checkCombosOnUp(now)
                }
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (action == KeyEvent.ACTION_DOWN) {
                    isVolDownPressed = true
                    volDownPressTime = now
                    return checkCombosOnDown(now)
                } else if (action == KeyEvent.ACTION_UP) {
                    isVolDownPressed = false
                    return checkCombosOnUp(now)
                }
            }

            KeyEvent.KEYCODE_POWER -> {
                if (action == KeyEvent.ACTION_DOWN) {
                    isPowerPressed = true
                    powerPressTime = now
                    return checkCombosOnDown(now)
                } else if (action == KeyEvent.ACTION_UP) {
                    isPowerPressed = false
                    return checkCombosOnUp(now)
                }
            }
        }

        return false
    }

    private fun checkCombosOnDown(now: Long): Boolean {
        // Combo: Volume Up + Volume Down
        if (isVolUpPressed && isVolDownPressed) {
            if (!isVolUpDownTriggered) {
                isVolUpDownTriggered = true
                if (isTriggerActive(TriggerType.VOLUME_UP_PLUS_DOWN)) {
                    onTriggerDetected(TriggerType.VOLUME_UP_PLUS_DOWN)
                }
            }
            return true
        }

        // Combo: Volume Up + Power
        if (isVolUpPressed && isPowerPressed) {
            handleVolPowerComboDown(now)
            return true
        }

        return false
    }

    private fun checkCombosOnUp(now: Long): Boolean {
        if (isVolUpDownTriggered) {
            if (!isVolUpPressed && !isVolDownPressed) {
                isVolUpDownTriggered = false
            }
            return true
        }

        if (isVolPowerBothDown) {
            handleVolPowerComboUp(now)
            return true
        }

        return false
    }

    private fun handleVolPowerComboDown(now: Long) {
        if (!isVolPowerBothDown) {
            isVolPowerBothDown = true
            isVolPowerHoldTriggered = false
            handler.removeCallbacks(volPowerSinglePressRunnable)

            handler.postDelayed(volPowerHoldRunnable, HOLD_TIMEOUT_MS)
        }
    }

    private fun handleVolPowerComboUp(now: Long) {
        isVolPowerBothDown = false
        handler.removeCallbacks(volPowerHoldRunnable)

        if (isVolPowerHoldTriggered) {
            isVolPowerHoldTriggered = false
            return
        }

        if (now - lastVolPowerComboReleaseTime <= DOUBLE_PRESS_TIMEOUT_MS) {
            handler.removeCallbacks(volPowerSinglePressRunnable)
            lastVolPowerComboReleaseTime = 0L
            if (isTriggerActive(TriggerType.VOLUME_UP_PLUS_POWER_DOUBLE)) {
                onTriggerDetected(TriggerType.VOLUME_UP_PLUS_POWER_DOUBLE)
            }
        } else {
            lastVolPowerComboReleaseTime = now
            if (isTriggerActive(TriggerType.VOLUME_UP_PLUS_POWER_SINGLE)) {
                handler.postDelayed(volPowerSinglePressRunnable, DOUBLE_PRESS_TIMEOUT_MS)
            }
        }
    }

    companion object {
        private const val HOLD_NAV_BUTTON_TIMEOUT_MS = 450L
        private const val DOUBLE_PRESS_TIMEOUT_MS = 380L
        private const val HOLD_TIMEOUT_MS = 500L
    }
}
