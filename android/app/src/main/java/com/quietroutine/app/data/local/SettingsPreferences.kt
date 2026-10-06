package com.quietroutine.app.data.local

import android.content.Context
import com.quietroutine.app.core.config.GameConfig

class SettingsPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var autoAdvance: Boolean
        get() = prefs.getBoolean(KEY_AUTO_ADVANCE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_ADVANCE, value).apply()
        }

    var keepAwake: Boolean
        get() = prefs.getBoolean(KEY_KEEP_AWAKE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_KEEP_AWAKE, value).apply()
        }

    var reducedMotion: Boolean
        get() = prefs.getBoolean(KEY_REDUCED_MOTION, false)
        set(value) {
            prefs.edit().putBoolean(KEY_REDUCED_MOTION, value).apply()
        }

    var defaultStepMinutes: Int
        get() = prefs.getInt(KEY_STEP_MINUTES, GameConfig.DEFAULT_STEP_MINUTES)
        set(value) {
            prefs.edit().putInt(KEY_STEP_MINUTES, value).apply()
        }

    companion object {
        private const val FILE_NAME = "quiet_routine_settings"
        private const val KEY_AUTO_ADVANCE = "auto_advance"
        private const val KEY_KEEP_AWAKE = "keep_awake"
        private const val KEY_REDUCED_MOTION = "reduced_motion"
        private const val KEY_STEP_MINUTES = "step_minutes"
    }
}
