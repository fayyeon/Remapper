package com.remapper.app

import android.app.Application
import com.google.android.material.color.DynamicColors

/**
 * Remapper Application entry point.
 * Follows Google's Material Expressive 3 theming scheme and automatically
 * applies dynamic wallpaper-based theming (Monet) on supported Android versions.
 */
class RemapperApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Automatically apply dynamic color palette (Material You / Expressive 3)
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    companion object {
        lateinit var instance: RemapperApp
            private set
    }
}
