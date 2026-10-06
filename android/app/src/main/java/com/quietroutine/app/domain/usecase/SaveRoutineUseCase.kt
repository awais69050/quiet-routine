package com.quietroutine.app.domain.usecase

import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.repository.RoutineRepository

class SaveRoutineUseCase(private val repository: RoutineRepository) {

    operator fun invoke(routine: Routine): Boolean {
        if (routine.name.isBlank()) return false
        if (routine.steps.isEmpty()) return false
        repository.save(routine)
        return true
    }
}
