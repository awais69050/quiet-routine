package com.quietroutine.app.domain.model

data class Routine(
    val id: String,
    val name: String,
    val category: RoutineCategory,
    val steps: List<RoutineStep>
) {
    val stepCount: Int
        get() = steps.size

    val totalMinutes: Int
        get() = steps.sumOf { it.minutes }
}
