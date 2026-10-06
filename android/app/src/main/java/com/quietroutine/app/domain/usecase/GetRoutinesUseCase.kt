package com.quietroutine.app.domain.usecase

import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.repository.RoutineRepository

class GetRoutinesUseCase(private val repository: RoutineRepository) {

    operator fun invoke(): List<Routine> = repository.routines()

    fun byId(id: String): Routine? = repository.routineById(id)
}
