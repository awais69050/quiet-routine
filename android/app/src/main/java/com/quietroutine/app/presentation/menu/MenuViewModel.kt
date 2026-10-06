package com.quietroutine.app.presentation.menu

import androidx.lifecycle.ViewModel
import com.quietroutine.app.core.session.RunSession
import com.quietroutine.app.domain.usecase.GetConsistencyStatsUseCase
import com.quietroutine.app.domain.usecase.GetRoutinesUseCase
import com.quietroutine.app.domain.usecase.GetRunHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val getRoutines: GetRoutinesUseCase,
    private val consistencyStats: GetConsistencyStatsUseCase,
    private val runHistory: GetRunHistoryUseCase,
    private val session: RunSession
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())

    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    fun refresh() {
        val routines = getRoutines()
        val stats = consistencyStats()
        val stored = session.selectedRoutineId()
        val selected = when {
            routines.any { it.id == stored } -> stored
            routines.isNotEmpty() -> routines.first().id
            else -> ""
        }
        if (selected.isNotEmpty()) session.selectRoutine(selected)
        val consistency = HashMap<String, Int>()
        routines.forEach { routine ->
            consistency[routine.id] = runHistory.percentFor(routine.id)
        }
        _state.value = MenuUiState(
            routines = routines,
            selectedId = selected,
            streak = stats.currentStreak,
            doneToday = stats.doneToday,
            routineCount = routines.size,
            consistency = consistency
        )
    }

    fun select(id: String) {
        session.selectRoutine(id)
        _state.value = _state.value.copy(selectedId = id)
    }

    fun selectedRoutineId(): String = _state.value.selectedId
}
