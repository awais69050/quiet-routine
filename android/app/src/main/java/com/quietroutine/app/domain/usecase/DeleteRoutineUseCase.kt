package com.quietroutine.app.domain.usecase

import com.quietroutine.app.domain.repository.RoutineRepository

class DeleteRoutineUseCase(private val repository: RoutineRepository) {

    operator fun invoke(id: String) {
        repository.delete(id)
    }
}
