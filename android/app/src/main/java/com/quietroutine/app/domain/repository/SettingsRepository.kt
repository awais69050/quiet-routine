package com.quietroutine.app.domain.repository

interface SettingsRepository {
    var autoAdvance: Boolean
    var keepAwake: Boolean
    var reducedMotion: Boolean
    var defaultStepMinutes: Int
}
