package com.quietroutine.app.presentation.splash

data class SplashUiState(
    val elapsedMillis: Long = 0L,
    val totalMillis: Long = 0L,
    val preparing: Boolean = true,
    val readyToAdvance: Boolean = false
) {
    val progressFraction: Float
        get() = if (totalMillis <= 0L) {
            0f
        } else {
            val raw = elapsedMillis.toFloat() / totalMillis.toFloat()
            if (raw > 1f) 1f else raw
        }

    val progressPercent: Int
        get() = (progressFraction * 100f).toInt()
}
