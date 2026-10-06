package com.quietroutine.app.domain.repository

import com.quietroutine.app.domain.model.RoutineRun

interface HistoryRepository {
    fun runs(): List<RoutineRun>
    fun record(run: RoutineRun)
    fun lastRun(): RoutineRun?
    fun clear()
    fun todayEpochDay(): Long
}
