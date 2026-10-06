package com.quietroutine.app.core.session

import com.quietroutine.app.domain.model.RoutineRun

class RunSession {

    private var selectedId: String = ""
    private var lastResult: RoutineRun? = null
    private var lastOutcomes: List<Pair<String, Boolean>> = emptyList()

    fun selectRoutine(id: String) {
        selectedId = id
    }

    fun selectedRoutineId(): String = selectedId

    fun storeResult(run: RoutineRun, outcomes: List<Pair<String, Boolean>>) {
        lastResult = run
        lastOutcomes = outcomes
    }

    fun result(): RoutineRun? = lastResult

    fun outcomes(): List<Pair<String, Boolean>> = lastOutcomes
}
