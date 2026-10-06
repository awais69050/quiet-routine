package com.quietroutine.app.domain.model

data class ConsistencyStats(
    val currentStreak: Int,
    val bestStreak: Int,
    val doneToday: Int,
    val totalRuns: Int,
    val averageCompletionPercent: Int,
    val dayCompletion: List<Float>
)
