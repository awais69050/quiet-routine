package com.quietroutine.app.domain.model

data class RoutineRun(
    val routineId: String,
    val routineName: String,
    val epochDay: Long,
    val completedSteps: Int,
    val skippedSteps: Int,
    val totalSteps: Int,
    val durationMillis: Long
) {
    val completionRate: Float
        get() = if (totalSteps <= 0) 0f else completedSteps.toFloat() / totalSteps.toFloat()
}
