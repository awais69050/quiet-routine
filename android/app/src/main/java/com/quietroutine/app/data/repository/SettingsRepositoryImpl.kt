package com.quietroutine.app.data.repository

import com.quietroutine.app.data.local.SettingsPreferences
import com.quietroutine.app.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: SettingsPreferences
) : SettingsRepository {

    override var autoAdvance: Boolean
        get() = preferences.autoAdvance
        set(value) {
            preferences.autoAdvance = value
        }

    override var keepAwake: Boolean
        get() = preferences.keepAwake
        set(value) {
            preferences.keepAwake = value
        }

    override var reducedMotion: Boolean
        get() = preferences.reducedMotion
        set(value) {
            preferences.reducedMotion = value
        }

    override var defaultStepMinutes: Int
        get() = preferences.defaultStepMinutes
        set(value) {
            preferences.defaultStepMinutes = value
        }
}
