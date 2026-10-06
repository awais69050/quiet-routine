package com.quietroutine.app.presentation.game

import com.quietroutine.app.domain.model.RoutineCategory
import com.quietroutine.app.domain.model.StepOutcome

data class GameUiState(
    val routineName: String = "",
    val stepName: String = "",
    val stepDetail: String = "",
    val stepMinutes: Int = 0,
    val category: RoutineCategory = RoutineCategory.MORNING,
    val stepNumber: Int = 0,
    val totalSteps: Int = 0,
    val remainingMillis: Long = 0L,
    val stepDurationMillis: Long = 1L,
    val outcomes: List<StepOutcome> = emptyList(),
    val completedSteps: Int = 0,
    val skippedSteps: Int = 0,
    val nextStepName: String = "",
    val finishing: Boolean = false,
    val navigateToResult: Boolean = false
) {
    val ringProgress: Int
        get() {
            if (stepDurationMillis <= 0L) return 0
            val used = stepDurationMillis - remainingMillis
            val fraction = used.toFloat() / stepDurationMillis.toFloat()
            val clamped = when {
                fraction < 0f -> 0f
                fraction > 1f -> 1f
                else -> fraction
            }
            return (clamped * 100f).toInt()
        }

    val hasNextStep: Boolean
        get() = nextStepName.isNotEmpty()
}
