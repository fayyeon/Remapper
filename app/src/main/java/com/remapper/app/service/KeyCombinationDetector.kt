package com.remapper.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.KeyEvent
import com.remapper.app.data.TriggerType

/**
 * High-performance state machine for detecting:
 * 1. Double press Home
 * 2. Single press Volume Up + Volume Down
 * 3. Single press Volume Up + Power
 * 4. Hold Volume Up + Power
 * 5. Double press Volume Up + Power
 */
class KeyCombinationDetector(
    private val context: Context,
    private val onTriggerDetected: (TriggerType) -> Unit
) {

    private val handler = Handler(Looper.getMainLooper())

    // Key states
    private var isVolUpPressed = false
    private var isVolDownPressed = false
    private var isPowerPressed = false

    private var volUpPressTime = 0L
    private var volDownPressTime = 0L
    private var powerPressTime = 0L

    // Double press Home tracking
    private var lastHomePressTime = 0L

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

    // Screen receiver to assist power key detection on devices where power events are filtered
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val now = SystemClock.uptimeMillis()
            // If Volume Up was pressed right before screen off/on, detect Vol Up + Power!
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
     * Called from AccessibilityService.onKeyEvent
     * Returns true if event is consumed, false otherwise.
     */
    fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val action = event.action
        val now = SystemClock.uptimeMillis()

        // 1. Double press Home
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            if (action == KeyEvent.ACTION_DOWN) {
                if (now - lastHomePressTime <= DOUBLE_PRESS_TIMEOUT_MS) {
                    lastHomePressTime = 0L
                    onTriggerDetected(TriggerType.DOUBLE_PRESS_HOME)
                    return true
                } else {
                    lastHomePressTime = now
                }
            }
            return false
        }

        // 2 & 3 & 4 & 5: Volume & Power combinations
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
                onTriggerDetected(TriggerType.VOLUME_UP_PLUS_DOWN)
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
        // Handle Volume Up + Volume Down release
        if (isVolUpDownTriggered) {
            if (!isVolUpPressed && !isVolDownPressed) {
                isVolUpDownTriggered = false
            }
            return true
        }

        // Handle Volume Up + Power release
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

            // Start hold timer
            handler.postDelayed(volPowerHoldRunnable, HOLD_TIMEOUT_MS)
        }
    }

    private fun handleVolPowerComboUp(now: Long) {
        isVolPowerBothDown = false
        handler.removeCallbacks(volPowerHoldRunnable)

        if (isVolPowerHoldTriggered) {
            // Already handled as hold
            isVolPowerHoldTriggered = false
            return
        }

        // Check if double press
        if (now - lastVolPowerComboReleaseTime <= DOUBLE_PRESS_TIMEOUT_MS) {
            // Double press detected!
            handler.removeCallbacks(volPowerSinglePressRunnable)
            lastVolPowerComboReleaseTime = 0L
            onTriggerDetected(TriggerType.VOLUME_UP_POWER_DOUBLE)
        } else {
            // Schedule single press check
            lastVolPowerComboReleaseTime = now
            handler.postDelayed(volPowerSinglePressRunnable, DOUBLE_PRESS_TIMEOUT_MS)
        }
    }

    companion object {
        private const val DOUBLE_PRESS_TIMEOUT_MS = 380L
        private const val HOLD_TIMEOUT_MS = 500L
    }
}
