package com.quietroutine.app.presentation.gameover

import androidx.lifecycle.ViewModel
import com.quietroutine.app.core.session.RunSession
import com.quietroutine.app.domain.usecase.GetConsistencyStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val session: RunSession,
    private val consistencyStats: GetConsistencyStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())

    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun load() {
        val run = session.result()
        val stats = consistencyStats()
        if (run == null) {
            _state.value = GameOverUiState(streak = stats.currentStreak)
            return
        }
        _state.value = GameOverUiState(
            routineName = run.routineName,
            completedSteps = run.completedSteps,
            skippedSteps = run.skippedSteps,
            totalSteps = run.totalSteps,
            durationMillis = run.durationMillis,
            streak = stats.currentStreak,
            steps = session.outcomes()
        )
    }
}
