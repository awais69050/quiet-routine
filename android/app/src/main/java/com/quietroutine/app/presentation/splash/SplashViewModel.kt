package com.quietroutine.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quietroutine.app.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        SplashUiState(
            elapsedMillis = 0L,
            totalMillis = GameConfig.LOADER_DURATION_MS,
            preparing = true,
            readyToAdvance = false
        )
    )

    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    private var handedOver = false

    fun start() {
        if (handedOver) return
        if (timerJob != null) return
        timerJob = viewModelScope.launch {
            val started = System.currentTimeMillis()
            var elapsed = 0L
            while (isActive && elapsed < GameConfig.LOADER_DURATION_MS) {
                delay(TICK_MS)
                elapsed = System.currentTimeMillis() - started
                val capped = if (elapsed > GameConfig.LOADER_DURATION_MS) {
                    GameConfig.LOADER_DURATION_MS
                } else {
                    elapsed
                }
                _state.value = _state.value.copy(
                    elapsedMillis = capped,
                    preparing = true
                )
            }
            _state.value = _state.value.copy(
                elapsedMillis = GameConfig.LOADER_DURATION_MS,
                preparing = false,
                readyToAdvance = true
            )
        }
    }

    fun consumeAdvance() {
        handedOver = true
        timerJob?.cancel()
        timerJob = null
        _state.value = _state.value.copy(readyToAdvance = false)
    }

    fun hasHandedOver(): Boolean = handedOver

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    private companion object {
        const val TICK_MS = 80L
    }
}
