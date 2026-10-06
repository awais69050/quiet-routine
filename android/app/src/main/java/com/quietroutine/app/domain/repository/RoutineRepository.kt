package com.quietroutine.app.domain.repository

import com.quietroutine.app.domain.model.Routine

interface RoutineRepository {
    fun routines(): List<Routine>
    fun routineById(id: String): Routine?
    fun save(routine: Routine)
    fun delete(id: String)
}
