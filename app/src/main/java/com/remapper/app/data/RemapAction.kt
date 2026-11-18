package com.remapper.app.data

import androidx.annotation.DrawableRes
import com.remapper.app.R
import org.json.JSONObject

enum class ActionType {
    QUICK_PANEL,
    APP_LAUNCH,
    APP_SHORTCUT,
    GLOBAL_ACTION
}

enum class ActionBadge {
    DIRECT,
    ADB_DIRECT,
    PANEL
}

/**
 * Represents a remap action that can be executed when a hardware button combo triggers.
 */
data class RemapAction(
    val id: String,
    val title: String,
    val description: String,
    val type: ActionType,
    @DrawableRes val iconRes: Int = R.drawable.ic_action_app,
    val packageName: String? = null,
    val className: String? = null,
    val intentAction: String? = null,
    val intentUri: String? = null,
    val badge: ActionBadge = ActionBadge.DIRECT,
    val requiresSecureSettings: Boolean = false
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("id", id)
        json.put("title", title)
        json.put("description", description)
        json.put("type", type.name)
        json.put("iconRes", iconRes)
        json.put("packageName", packageName ?: "")
        json.put("className", className ?: "")
        json.put("intentAction", intentAction ?: "")
        json.put("intentUri", intentUri ?: "")
        json.put("badge", badge.name)
        json.put("requiresSecureSettings", requiresSecureSettings)
        return json.toString()
    }

    companion object {
        fun fromJsonString(jsonStr: String?): RemapAction? {
            if (jsonStr.isNullOrEmpty()) return null
            return try {
                val json = JSONObject(jsonStr)
                RemapAction(
                    id = json.getString("id"),
                    title = json.getString("title"),
                    description = json.optString("description", ""),
                    type = ActionType.valueOf(json.getString("type")),
                    iconRes = json.optInt("iconRes", R.drawable.ic_action_app),
                    packageName = json.optString("packageName").takeIf { it.isNotEmpty() },
                    className = json.optString("className").takeIf { it.isNotEmpty() },
                    intentAction = json.optString("intentAction").takeIf { it.isNotEmpty() },
                    intentUri = json.optString("intentUri").takeIf { it.isNotEmpty() },
                    badge = ActionBadge.valueOf(json.optString("badge", ActionBadge.DIRECT.name)),
                    requiresSecureSettings = json.optBoolean("requiresSecureSettings", false)
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
