package com.quietroutine.app.domain.usecase

import com.quietroutine.app.domain.model.RoutineRun
import com.quietroutine.app.domain.repository.HistoryRepository

class RecordRunUseCase(private val repository: HistoryRepository) {

    operator fun invoke(run: RoutineRun) {
        repository.record(run)
    }

    fun last(): RoutineRun? = repository.lastRun()
}
