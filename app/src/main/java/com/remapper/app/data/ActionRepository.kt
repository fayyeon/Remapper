package com.remapper.app.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import com.remapper.app.R
import java.util.Locale

object ActionRepository {

    // Action ID Constants
    const val ID_AIRPLANE_MODE = "action_airplane_mode"
    const val ID_AUTO_ROTATE = "action_auto_rotate"
    const val ID_BATTERY_SAVER = "action_battery_saver"
    const val ID_BLUETOOTH = "action_bluetooth"
    const val ID_CAMERA_ACCESS = "action_camera_access"
    const val ID_DARK_MODE = "action_dark_mode"
    const val ID_DATA_SAVER = "action_data_saver"
    const val ID_DND = "action_dnd"
    const val ID_EXTRA_DIM = "action_extra_dim"
    const val ID_EYE_PROTECTION = "action_eye_protection"
    const val ID_FLASHLIGHT = "action_flashlight"
    const val ID_LOCATION = "action_location"
    const val ID_LOCK_SCREEN = "action_lock_screen"
    const val ID_MICROPHONE_ACCESS = "action_microphone_access"
    const val ID_MOBILE_DATA = "action_mobile_data"
    const val ID_NFC = "action_nfc"
    const val ID_NOTIFICATIONS = "action_notifications"
    const val ID_PERSONAL_HOTSPOT = "action_personal_hotspot"
    const val ID_POWER_MENU = "action_power_menu"
    const val ID_QUICK_SETTINGS = "action_quick_settings"
    const val ID_RINGER_CYCLE = "action_ringer_cycle"
    const val ID_RINGER_MUTE = "action_ringer_mute"
    const val ID_RINGER_VIBRATE = "action_ringer_vibrate"
    const val ID_SCREENSHOT = "action_screenshot"
    const val ID_SPLIT_SCREEN = "action_split_screen"
    const val ID_VOLUME_DIALOG = "action_volume_dialog"
    const val ID_WIFI = "action_wifi"

    /**
     * Complete list of Quick Panel and System Actions.
     * Sorted strictly in alphabetical order.
     */
    fun getQuickPanelActions(context: Context): List<RemapAction> {
        val list = listOf(
            RemapAction(
                id = ID_AIRPLANE_MODE,
                title = context.getString(R.string.action_airplane_mode),
                description = context.getString(R.string.action_airplane_mode_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_airplane,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_AUTO_ROTATE,
                title = context.getString(R.string.action_auto_rotate),
                description = context.getString(R.string.action_auto_rotate_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_auto_rotate,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_BATTERY_SAVER,
                title = context.getString(R.string.action_battery_saver),
                description = context.getString(R.string.action_battery_saver_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_battery,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_BLUETOOTH,
                title = context.getString(R.string.action_bluetooth),
                description = context.getString(R.string.action_bluetooth_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_bluetooth,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_CAMERA_ACCESS,
                title = context.getString(R.string.action_camera_access),
                description = context.getString(R.string.action_camera_access_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_camera,
                badge = ActionBadge.PANEL
            ),
            RemapAction(
                id = ID_DARK_MODE,
                title = context.getString(R.string.action_dark_mode),
                description = context.getString(R.string.action_dark_mode_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_dark_mode,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_DATA_SAVER,
                title = context.getString(R.string.action_data_saver),
                description = context.getString(R.string.action_data_saver_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_data_saver,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_DND,
                title = context.getString(R.string.action_dnd),
                description = context.getString(R.string.action_dnd_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_dnd,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_EXTRA_DIM,
                title = context.getString(R.string.action_extra_dim),
                description = context.getString(R.string.action_extra_dim_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_extra_dim,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_EYE_PROTECTION,
                title = context.getString(R.string.action_eye_protection),
                description = context.getString(R.string.action_eye_protection_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_eye_protection,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_FLASHLIGHT,
                title = context.getString(R.string.action_flashlight),
                description = context.getString(R.string.action_flashlight_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_flashlight,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_LOCATION,
                title = context.getString(R.string.action_location),
                description = context.getString(R.string.action_location_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_location,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_LOCK_SCREEN,
                title = context.getString(R.string.action_lock_screen),
                description = context.getString(R.string.action_lock_screen_desc),
                type = ActionType.GLOBAL_ACTION,
                iconRes = R.drawable.ic_action_lock_screen,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_MICROPHONE_ACCESS,
                title = context.getString(R.string.action_microphone_access),
                description = context.getString(R.string.action_microphone_access_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_mic,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_MOBILE_DATA,
                title = context.getString(R.string.action_mobile_data),
                description = context.getString(R.string.action_mobile_data_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_mobile_data,
                badge = ActionBadge.PANEL
            ),
            RemapAction(
                id = ID_NFC,
                title = context.getString(R.string.action_nfc),
                description = context.getString(R.string.action_nfc_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_nfc,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            ),
            RemapAction(
                id = ID_NOTIFICATIONS,
                title = context.getString(R.string.action_notifications),
                description = context.getString(R.string.action_notifications_desc),
                type = ActionType.GLOBAL_ACTION,
                iconRes = R.drawable.ic_action_notifications,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_PERSONAL_HOTSPOT,
                title = context.getString(R.string.action_personal_hotspot),
                description = context.getString(R.string.action_personal_hotspot_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_personal_hotspot,
                badge = ActionBadge.PANEL
            ),
            RemapAction(
                id = ID_POWER_MENU,
                title = context.getString(R.string.action_power_menu),
                description = context.getString(R.string.action_power_menu_desc),
                type = ActionType.GLOBAL_ACTION,
                iconRes = R.drawable.ic_action_power_menu,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_QUICK_SETTINGS,
                title = context.getString(R.string.action_quick_settings),
                description = context.getString(R.string.action_quick_settings_desc),
                type = ActionType.GLOBAL_ACTION,
                iconRes = R.drawable.ic_action_quick_settings,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_RINGER_CYCLE,
                title = context.getString(R.string.action_ringer_cycle),
                description = context.getString(R.string.action_ringer_cycle_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_ringer,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_RINGER_MUTE,
                title = context.getString(R.string.action_ringer_mute),
                description = context.getString(R.string.action_ringer_mute_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_ringer_mute,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_RINGER_VIBRATE,
                title = context.getString(R.string.action_ringer_vibrate),
                description = context.getString(R.string.action_ringer_vibrate_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_ringer_vibrate,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_SCREENSHOT,
                title = context.getString(R.string.action_screenshot),
                description = context.getString(R.string.action_screenshot_desc),
                type = ActionType.GLOBAL_ACTION,
                iconRes = R.drawable.ic_action_screenshot,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_SPLIT_SCREEN,
                title = context.getString(R.string.action_split_screen),
                description = context.getString(R.string.action_split_screen_desc),
                type = ActionType.GLOBAL_ACTION,
                iconRes = R.drawable.ic_action_split_screen,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_VOLUME_DIALOG,
                title = context.getString(R.string.action_volume_dialog),
                description = context.getString(R.string.action_volume_dialog_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_volume_dialog,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = ID_WIFI,
                title = context.getString(R.string.action_wifi),
                description = context.getString(R.string.action_wifi_desc),
                type = ActionType.QUICK_PANEL,
                iconRes = R.drawable.ic_action_wifi,
                badge = ActionBadge.ADB_DIRECT,
                requiresSecureSettings = true
            )
        )

        // Strict alphabetical sort
        return list.sortedBy { it.title.lowercase(Locale.ROOT) }
    }

    /**
     * Retrieves all installed launchable applications, sorted alphabetically by app label.
     */
    fun getInstalledApps(context: Context): List<RemapAction> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val appActions = resolveInfos.mapNotNull { ri ->
            val pkg = ri.activityInfo?.packageName ?: return@mapNotNull null
            val cls = ri.activityInfo?.name ?: return@mapNotNull null
            val label = ri.loadLabel(pm).toString()
            RemapAction(
                id = "app:$pkg",
                title = label,
                description = pkg,
                type = ActionType.APP_LAUNCH,
                iconRes = R.drawable.ic_action_app,
                packageName = pkg,
                className = cls,
                badge = ActionBadge.DIRECT
            )
        }.distinctBy { it.packageName }

        // Strict alphabetical sort
        return appActions.sortedBy { it.title.lowercase(Locale.ROOT) }
    }

    /**
     * Curated native actions and deep shortcuts (e.g., WhatsApp camera, Settings battery page,
     * Camera photo/video, Clock alarm, Google Assistant, etc.).
     * Sorted strictly in alphabetical order.
     */
    fun getNativeAppActions(context: Context): List<RemapAction> {
        val list = mutableListOf(
            RemapAction(
                id = "shortcut:settings_battery",
                title = "Battery Settings",
                description = "Open battery stats and saver page directly in Settings",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_battery,
                intentAction = Settings.ACTION_BATTERY_SAVER_SETTINGS,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:camera_photo",
                title = "Camera: Take Photo",
                description = "Open the native camera in still photo capture mode",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_camera,
                intentAction = "android.media.action.STILL_IMAGE_CAMERA",
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:camera_video",
                title = "Camera: Record Video",
                description = "Open the native camera in video recording mode",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_camera,
                intentAction = "android.media.action.VIDEO_CAMERA",
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:clock_alarm",
                title = "Clock: Set Alarm",
                description = "Open clock alarms directly",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_shortcut,
                intentAction = "android.intent.action.SHOW_ALARMS",
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:settings_display",
                title = "Display Settings",
                description = "Open display brightness and resolution page in Settings",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_extra_dim,
                intentAction = Settings.ACTION_DISPLAY_SETTINGS,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:google_assistant",
                title = "Google Assistant / Voice Search",
                description = "Trigger system voice assistant directly",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_mic,
                intentAction = Intent.ACTION_VOICE_COMMAND,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:settings_network",
                title = "Network & Internet Settings",
                description = "Open network connectivity hub in Settings",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_wifi,
                intentAction = Settings.ACTION_WIRELESS_SETTINGS,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:settings_privacy",
                title = "Privacy & Permission Settings",
                description = "Open permission manager and sensor toggles in Settings",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_lock_screen,
                intentAction = Settings.ACTION_PRIVACY_SETTINGS,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:settings_sound",
                title = "Sound & Vibration Settings",
                description = "Open volume and audio profile page in Settings",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_ringer,
                intentAction = Settings.ACTION_SOUND_SETTINGS,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:settings_storage",
                title = "Storage Settings",
                description = "Open storage manager and free-up-space page in Settings",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_shortcut,
                intentAction = Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
                badge = ActionBadge.DIRECT
            ),
            RemapAction(
                id = "shortcut:whatsapp_camera",
                title = "WhatsApp: Direct Camera",
                description = "Open WhatsApp instant camera activity directly",
                type = ActionType.APP_SHORTCUT,
                iconRes = R.drawable.ic_action_camera,
                packageName = "com.whatsapp",
                className = "com.whatsapp.camera.CameraActivity",
                intentAction = "android.intent.action.MAIN",
                badge = ActionBadge.DIRECT
            )
        )

        // Strict alphabetical sort
        return list.sortedBy { it.title.lowercase(Locale.ROOT) }
    }
}
