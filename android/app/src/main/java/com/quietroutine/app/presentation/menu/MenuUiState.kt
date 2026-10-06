package com.quietroutine.app.presentation.menu

import com.quietroutine.app.domain.model.Routine

data class MenuUiState(
    val routines: List<Routine> = emptyList(),
    val selectedId: String = "",
    val streak: Int = 0,
    val doneToday: Int = 0,
    val routineCount: Int = 0,
    val consistency: Map<String, Int> = emptyMap()
) {
    val hasRoutines: Boolean
        get() = routines.isNotEmpty()
}
