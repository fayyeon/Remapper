package com.remapper.app.service

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.UiModeManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Toast
import com.remapper.app.data.ActionRepository
import com.remapper.app.data.ActionType
import com.remapper.app.data.PreferencesManager
import com.remapper.app.data.RemapAction

/**
 * Executes mapped actions for hardware button combos.
 * Directly toggles non-root features where allowed, uses WRITE_SECURE_SETTINGS if granted via ADB,
 * and seamlessly invokes native floating panels / settings slices where restricted.
 */
class ActionExecutor(private val service: AccessibilityService) {

    private val context: Context get() = service

    // Torch state tracking
    private var isTorchOn: Boolean = false
    private var torchCameraId: String? = null

    init {
        initTorchState()
    }

    private fun initTorchState() {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
                override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                    super.onTorchModeChanged(cameraId, enabled)
                    if (cameraId == torchCameraId) {
                        isTorchOn = enabled
                    }
                }
            }, null)

            torchCameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: cameraManager.cameraIdList.firstOrNull()
        } catch (_: Exception) {}
    }

    fun execute(action: RemapAction) {
        // Trigger haptic feedback if enabled
        if (PreferencesManager.getInstance(context).isHapticEnabled()) {
            performHapticFeedback()
        }

        when (action.type) {
            ActionType.QUICK_PANEL -> executeQuickPanelAction(action.id)
            ActionType.GLOBAL_ACTION -> executeGlobalAction(action.id)
            ActionType.APP_LAUNCH -> executeAppLaunch(action)
            ActionType.APP_SHORTCUT -> executeAppShortcut(action)
        }
    }

    private fun executeQuickPanelAction(actionId: String) {
        when (actionId) {
            ActionRepository.ID_AIRPLANE_MODE -> toggleAirplaneMode()
            ActionRepository.ID_AUTO_ROTATE -> toggleAutoRotate()
            ActionRepository.ID_BATTERY_SAVER -> toggleBatterySaver()
            ActionRepository.ID_BLUETOOTH -> toggleBluetooth()
            ActionRepository.ID_CAMERA_ACCESS -> toggleCameraAccess()
            ActionRepository.ID_DARK_MODE -> toggleDarkMode()
            ActionRepository.ID_DATA_SAVER -> toggleDataSaver()
            ActionRepository.ID_DND -> toggleDnd()
            ActionRepository.ID_EXTRA_DIM -> toggleExtraDim()
            ActionRepository.ID_EYE_PROTECTION -> toggleEyeProtection()
            ActionRepository.ID_FLASHLIGHT -> toggleFlashlight()
            ActionRepository.ID_LOCATION -> toggleLocation()
            ActionRepository.ID_MICROPHONE_ACCESS -> toggleMicrophoneAccess()
            ActionRepository.ID_MOBILE_DATA -> toggleMobileData()
            ActionRepository.ID_NFC -> toggleNfc()
            ActionRepository.ID_PERSONAL_HOTSPOT -> togglePersonalHotspot()
            ActionRepository.ID_RINGER_CYCLE -> cycleRingerMode()
            ActionRepository.ID_RINGER_MUTE -> setRingerMode(AudioManager.RINGER_MODE_SILENT)
            ActionRepository.ID_RINGER_VIBRATE -> setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
            ActionRepository.ID_VOLUME_DIALOG -> showVolumeDialog()
            ActionRepository.ID_WIFI -> toggleWifi()
        }
    }

    private fun executeGlobalAction(actionId: String) {
        when (actionId) {
            ActionRepository.ID_LOCK_SCREEN -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
                }
            }
            ActionRepository.ID_NOTIFICATIONS -> {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
            }
            ActionRepository.ID_POWER_MENU -> {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
            }
            ActionRepository.ID_QUICK_SETTINGS -> {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)
            }
            ActionRepository.ID_SCREENSHOT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
                }
            }
            ActionRepository.ID_SPLIT_SCREEN -> {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
            }
        }
    }

    // Direct / ADB / Panel implementations

    private fun toggleFlashlight() {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val id = torchCameraId ?: return
            isTorchOn = !isTorchOn
            cameraManager.setTorchMode(id, isTorchOn)
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleAutoRotate() {
        if (!Settings.System.canWrite(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Grant Write Settings permission for Auto-Rotate", Toast.LENGTH_LONG).show()
            return
        }
        try {
            val current = Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0)
            val newState = if (current == 1) 0 else 1
            Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, newState)
            Toast.makeText(context, if (newState == 1) "Auto-rotate Enabled" else "Auto-rotate Disabled", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to toggle rotation: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cycleRingerMode() {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val nextMode = when (am.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> AudioManager.RINGER_MODE_VIBRATE
            AudioManager.RINGER_MODE_VIBRATE -> AudioManager.RINGER_MODE_SILENT
            else -> AudioManager.RINGER_MODE_NORMAL
        }
        setRingerMode(nextMode)
    }

    private fun setRingerMode(mode: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!nm.isNotificationPolicyAccessGranted && mode == AudioManager.RINGER_MODE_SILENT) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Grant DND Policy access to enable Silent Mode", Toast.LENGTH_LONG).show()
            return
        }
        try {
            am.ringerMode = mode
            val label = when (mode) {
                AudioManager.RINGER_MODE_NORMAL -> "Normal Sound"
                AudioManager.RINGER_MODE_VIBRATE -> "Vibrate Only"
                AudioManager.RINGER_MODE_SILENT -> "Silent Mode"
                else -> "Ringer Mode Changed"
            }
            Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error setting ringer mode: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleDnd() {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!nm.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(context, "Grant DND Policy access to toggle Do Not Disturb", Toast.LENGTH_LONG).show()
            return
        }
        val current = nm.currentInterruptionFilter
        val newFilter = if (current == NotificationManager.INTERRUPTION_FILTER_ALL) {
            NotificationManager.INTERRUPTION_FILTER_PRIORITY
        } else {
            NotificationManager.INTERRUPTION_FILTER_ALL
        }
        nm.setInterruptionFilter(newFilter)
        val text = if (newFilter == NotificationManager.INTERRUPTION_FILTER_ALL) "DND Off" else "DND On"
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun toggleDarkMode() {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val isNight = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        val newMode = if (isNight) UiModeManager.MODE_NIGHT_NO else UiModeManager.MODE_NIGHT_YES
        uiModeManager.nightMode = newMode
        Toast.makeText(context, if (isNight) "Light Mode" else "Dark Mode", Toast.LENGTH_SHORT).show()
    }

    private fun toggleMicrophoneAccess() {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val newMute = !am.isMicrophoneMute
        am.isMicrophoneMute = newMute
        Toast.makeText(context, if (newMute) "Microphone Muted" else "Microphone Unmuted", Toast.LENGTH_SHORT).show()
    }

    private fun showVolumeDialog() {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI)
    }

    @SuppressLint("MissingPermission")
    private fun toggleBluetooth() {
        try {
            val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            val adapter = bm?.adapter ?: BluetoothAdapter.getDefaultAdapter()
            if (adapter != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                if (adapter.isEnabled) adapter.disable() else adapter.enable()
                return
            }
        } catch (_: Exception) {}
        // Fallback to native Bluetooth panel
        launchIntent(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    private fun toggleWifi() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Global.getInt(context.contentResolver, "wifi_on", 0)
                val next = if (current == 1) 0 else 1
                Settings.Global.putInt(context.contentResolver, "wifi_on", next)
                Toast.makeText(context, if (next == 1) "Wi-Fi On" else "Wi-Fi Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        // Floating native Internet slice panel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            launchIntent(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        } else {
            launchIntent(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
    }

    private fun toggleAirplaneMode() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0)
                val next = if (current == 1) 0 else 1
                Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, next)
                val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).apply {
                    putExtra("state", next == 1)
                }
                context.sendBroadcast(intent)
                Toast.makeText(context, if (next == 1) "Airplane Mode On" else "Airplane Mode Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        launchIntent(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
    }

    private fun toggleBatterySaver() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Global.getInt(context.contentResolver, "low_power", 0)
                val next = if (current == 1) 0 else 1
                Settings.Global.putInt(context.contentResolver, "low_power", next)
                Toast.makeText(context, if (next == 1) "Battery Saver On" else "Battery Saver Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        launchIntent(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
    }

    private fun toggleLocation() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                val next = if (current != Settings.Secure.LOCATION_MODE_OFF) {
                    Settings.Secure.LOCATION_MODE_OFF
                } else {
                    Settings.Secure.LOCATION_MODE_HIGH_ACCURACY
                }
                Settings.Secure.putInt(context.contentResolver, Settings.Secure.LOCATION_MODE, next)
                Toast.makeText(context, if (next != Settings.Secure.LOCATION_MODE_OFF) "GPS Location On" else "GPS Location Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        launchIntent(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun toggleNfc() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Global.getInt(context.contentResolver, "nfc_on", 0)
                val next = if (current == 1) 0 else 1
                Settings.Global.putInt(context.contentResolver, "nfc_on", next)
                Toast.makeText(context, if (next == 1) "NFC On" else "NFC Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        launchIntent(Intent(Settings.ACTION_NFC_SETTINGS))
    }

    private fun toggleExtraDim() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Secure.getInt(context.contentResolver, "reduce_bright_colors_activated", 0)
                val next = if (current == 1) 0 else 1
                Settings.Secure.putInt(context.contentResolver, "reduce_bright_colors_activated", next)
                Toast.makeText(context, if (next == 1) "Extra Dim On" else "Extra Dim Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            launchIntent(Intent(Settings.ACTION_REDUCE_BRIGHT_COLORS_SETTINGS))
        } else {
            launchIntent(Intent(Settings.ACTION_DISPLAY_SETTINGS))
        }
    }

    private fun toggleEyeProtection() {
        if (hasSecureSettingsPermission()) {
            try {
                val current = Settings.Secure.getInt(context.contentResolver, "night_display_activated", 0)
                val next = if (current == 1) 0 else 1
                Settings.Secure.putInt(context.contentResolver, "night_display_activated", next)
                Toast.makeText(context, if (next == 1) "Night Light On" else "Night Light Off", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }
        launchIntent(Intent(Settings.ACTION_NIGHT_DISPLAY_SETTINGS))
    }

    private fun toggleCameraAccess() {
        launchIntent(Intent(Settings.ACTION_PRIVACY_SETTINGS))
    }

    private fun toggleMobileData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            launchIntent(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        } else {
            launchIntent(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
        }
    }

    private fun toggleDataSaver() {
        launchIntent(Intent(Settings.ACTION_DATA_SAVER_SETTINGS))
    }

    private fun togglePersonalHotspot() {
        val intent = Intent("android.settings.TETHER_SETTINGS")
        if (intent.resolveActivity(context.packageManager) != null) {
            launchIntent(intent)
        } else {
            launchIntent(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    // App & Shortcut Execution

    private fun executeAppLaunch(action: RemapAction) {
        val pkg = action.packageName ?: return
        val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "App not installed: $pkg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeAppShortcut(action: RemapAction) {
        try {
            val intent = if (!action.packageName.isNullOrEmpty() && !action.className.isNullOrEmpty()) {
                Intent().apply {
                    component = ComponentName(action.packageName, action.className)
                    action.intentAction?.let { setAction(it) }
                }
            } else if (!action.intentAction.isNullOrEmpty()) {
                Intent(action.intentAction)
            } else if (!action.intentUri.isNullOrEmpty()) {
                Intent.parseUri(action.intentUri, Intent.URI_INTENT_SCHEME)
            } else {
                null
            }

            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            } ?: Toast.makeText(context, "Action not found", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to launch action: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchIntent(intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open settings: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasSecureSettingsPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    private fun performHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(45)
                }
            }
        } catch (_: Exception) {}
    }
}
