package com.remapper.app.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.remapper.app.R

/**
 * Enumerates the 5 supported hardware button trigger combinations requested:
 * 1. Double press the home button
 * 2. Single press volume up + volume down button
 * 3. Single press volume up + power button
 * 4. Hold volume up + power button
 * 5. Double press volume up + power button
 */
enum class TriggerType(
    val key: String,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    @DrawableRes val iconRes: Int
) {
    DOUBLE_PRESS_HOME(
        key = "trigger_double_press_home",
        titleRes = R.string.trigger_double_press_home,
        descRes = R.string.trigger_double_press_home_desc,
        iconRes = R.drawable.ic_trigger_home
    ),

    VOLUME_UP_PLUS_DOWN(
        key = "trigger_volume_up_plus_down",
        titleRes = R.string.trigger_volume_up_plus_down,
        descRes = R.string.trigger_volume_up_plus_down_desc,
        iconRes = R.drawable.ic_trigger_volume_combo
    ),

    VOLUME_UP_PLUS_POWER_SINGLE(
        key = "trigger_volume_up_power_single",
        titleRes = R.string.trigger_volume_up_power_single,
        descRes = R.string.trigger_volume_up_power_single_desc,
        iconRes = R.drawable.ic_trigger_vol_power_single
    ),

    VOLUME_UP_PLUS_POWER_HOLD(
        key = "trigger_volume_up_power_hold",
        titleRes = R.string.trigger_volume_up_power_hold,
        descRes = R.string.trigger_volume_up_power_hold_desc,
        iconRes = R.drawable.ic_trigger_vol_power_hold
    ),

    VOLUME_UP_PLUS_POWER_DOUBLE(
        key = "trigger_volume_up_power_double",
        titleRes = R.string.trigger_volume_up_power_double,
        descRes = R.string.trigger_volume_up_power_double_desc,
        iconRes = R.drawable.ic_trigger_vol_power_double
    );

    companion object {
        fun fromKey(key: String): TriggerType? = entries.find { it.key == key }
    }
}
