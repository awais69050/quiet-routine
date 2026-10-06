package com.quietroutine.app.presentation.gameover

data class GameOverUiState(
    val routineName: String = "",
    val completedSteps: Int = 0,
    val skippedSteps: Int = 0,
    val totalSteps: Int = 0,
    val durationMillis: Long = 0L,
    val streak: Int = 0,
    val steps: List<Pair<String, Boolean>> = emptyList()
) {
    val perfect: Boolean
        get() = totalSteps > 0 && completedSteps == totalSteps

    val ringTarget: Int
        get() = if (totalSteps <= 0) 0 else (completedSteps * 100) / totalSteps
}
