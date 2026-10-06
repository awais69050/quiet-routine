package com.quietroutine.app.domain.usecase

import com.quietroutine.app.domain.model.ConsistencyStats
import com.quietroutine.app.domain.model.RoutineRun
import com.quietroutine.app.domain.repository.HistoryRepository

class GetConsistencyStatsUseCase(private val repository: HistoryRepository) {

    operator fun invoke(): ConsistencyStats {
        val runs = repository.runs()
        val today = repository.todayEpochDay()
        if (runs.isEmpty()) {
            return ConsistencyStats(
                currentStreak = 0,
                bestStreak = 0,
                doneToday = 0,
                totalRuns = 0,
                averageCompletionPercent = 0,
                dayCompletion = List(WINDOW_DAYS) { 0f }
            )
        }
        val qualifying = runs
            .filter { it.completionRate >= QUALIFY_RATE }
            .map { it.epochDay }
            .toSet()
        return ConsistencyStats(
            currentStreak = currentStreak(qualifying, today),
            bestStreak = bestStreak(qualifying),
            doneToday = runs.filter { it.epochDay == today }.sumOf { it.completedSteps },
            totalRuns = runs.size,
            averageCompletionPercent = averagePercent(runs),
            dayCompletion = dayCompletion(runs, today)
        )
    }

    private fun currentStreak(days: Set<Long>, today: Long): Int {
        if (days.isEmpty()) return 0
        var anchor = if (days.contains(today)) today else today - 1
        if (!days.contains(anchor)) return 0
        var streak = 0
        while (days.contains(anchor)) {
            streak++
            anchor--
        }
        return streak
    }

    private fun bestStreak(days: Set<Long>): Int {
        if (days.isEmpty()) return 0
        val sorted = days.sorted()
        var best = 1
        var current = 1
        for (index in 1 until sorted.size) {
            current = if (sorted[index] == sorted[index - 1] + 1) current + 1 else 1
            if (current > best) best = current
        }
        return best
    }

    private fun averagePercent(runs: List<RoutineRun>): Int {
        val window = runs.take(WINDOW_DAYS)
        if (window.isEmpty()) return 0
        val total = window.sumOf { (it.completionRate * 100f).toDouble() }
        return (total / window.size).toInt()
    }

    private fun dayCompletion(runs: List<RoutineRun>, today: Long): List<Float> {
        val byDay = HashMap<Long, Float>()
        for (run in runs) {
            val existing = byDay[run.epochDay] ?: 0f
            if (run.completionRate > existing) byDay[run.epochDay] = run.completionRate
        }
        val result = ArrayList<Float>(WINDOW_DAYS)
        for (offset in WINDOW_DAYS - 1 downTo 0) {
            result.add(byDay[today - offset] ?: 0f)
        }
        return result
    }

    companion object {
        const val WINDOW_DAYS = 14
        private const val QUALIFY_RATE = 0.6f
    }
}
