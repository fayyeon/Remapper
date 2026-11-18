package com.remapper.app.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.remapper.app.R

/**
 * Enumerates hardware button trigger combinations:
 * Navigation buttons (3-button navigation mode):
 * - Hold Home button (overrides Google Assistant)
 * - Hold Recent Apps button
 * - Hold Back button
 *
 * Volume & Power combinations:
 * - Single press Volume Up + Volume Down
 * - Single press Volume Up + Power
 * - Hold Volume Up + Power
 * - Double press Volume Up + Power
 */
enum class TriggerType(
    val key: String,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    @DrawableRes val iconRes: Int,
    val is3ButtonNavOnly: Boolean = false
) {
    HOLD_HOME(
        key = "trigger_hold_home",
        titleRes = R.string.trigger_hold_home,
        descRes = R.string.trigger_hold_home_desc,
        iconRes = R.drawable.ic_trigger_home,
        is3ButtonNavOnly = true
    ),

    HOLD_RECENTS(
        key = "trigger_hold_recents",
        titleRes = R.string.trigger_hold_recents,
        descRes = R.string.trigger_hold_recents_desc,
        iconRes = R.drawable.ic_trigger_recents,
        is3ButtonNavOnly = true
    ),

    HOLD_BACK(
        key = "trigger_hold_back",
        titleRes = R.string.trigger_hold_back,
        descRes = R.string.trigger_hold_back_desc,
        iconRes = R.drawable.ic_trigger_back,
        is3ButtonNavOnly = true
    ),

    VOLUME_UP_PLUS_DOWN(
        key = "trigger_volume_up_plus_down",
        titleRes = R.string.trigger_volume_up_plus_down,
        descRes = R.string.trigger_volume_up_plus_down_desc,
        iconRes = R.drawable.ic_trigger_volume_combo,
        is3ButtonNavOnly = false
    ),

    VOLUME_UP_PLUS_POWER_SINGLE(
        key = "trigger_volume_up_power_single",
        titleRes = R.string.trigger_volume_up_power_single,
        descRes = R.string.trigger_volume_up_power_single_desc,
        iconRes = R.drawable.ic_trigger_vol_power_single,
        is3ButtonNavOnly = false
    ),

    VOLUME_UP_PLUS_POWER_HOLD(
        key = "trigger_volume_up_power_hold",
        titleRes = R.string.trigger_volume_up_power_hold,
        descRes = R.string.trigger_volume_up_power_hold_desc,
        iconRes = R.drawable.ic_trigger_vol_power_hold,
        is3ButtonNavOnly = false
    ),

    VOLUME_UP_PLUS_POWER_DOUBLE(
        key = "trigger_volume_up_power_double",
        titleRes = R.string.trigger_volume_up_power_double,
        descRes = R.string.trigger_volume_up_power_double_desc,
        iconRes = R.drawable.ic_trigger_vol_power_double,
        is3ButtonNavOnly = false
    );

    companion object {
        fun fromKey(key: String): TriggerType? = entries.find { it.key == key }
    }
}
