package com.quietroutine.app.domain.usecase

import com.quietroutine.app.domain.model.RoutineRun
import com.quietroutine.app.domain.repository.HistoryRepository

class GetRunHistoryUseCase(private val repository: HistoryRepository) {

    operator fun invoke(): List<RoutineRun> = repository.runs()

    fun today(): Long = repository.todayEpochDay()

    fun clear() {
        repository.clear()
    }

    fun percentFor(routineId: String): Int {
        val runs = repository.runs().filter { it.routineId == routineId }
        if (runs.isEmpty()) return 0
        val total = runs.sumOf { (it.completionRate * 100f).toDouble() }
        return (total / runs.size).toInt()
    }
}
