package com.quietroutine.app.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quietroutine.app.core.config.GameConfig
import com.quietroutine.app.core.session.RunSession
import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.model.RoutineRun
import com.quietroutine.app.domain.model.StepOutcome
import com.quietroutine.app.domain.repository.SettingsRepository
import com.quietroutine.app.domain.usecase.GetRoutinesUseCase
import com.quietroutine.app.domain.usecase.RecordRunUseCase
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val getRoutines: GetRoutinesUseCase,
    private val recordRun: RecordRunUseCase,
    private val session: RunSession,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())

    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var routine: Routine? = null

    private val outcomes = ArrayList<StepOutcome>()

    private var currentIndex = 0

    private var roundStartedAt = 0L

    private var stepStartedAt = 0L

    private var loopJob: Job? = null

    private var finished = false

    private var navigationConsumed = false

    fun start() {
        if (loopJob != null) return
        if (finished) return
        val routines = getRoutines()
        if (routines.isEmpty()) return
        val selected = getRoutines.byId(session.selectedRoutineId()) ?: routines.first()
        routine = selected
        outcomes.clear()
        selected.steps.forEachIndexed { index, _ ->
            outcomes.add(if (index == 0) StepOutcome.CURRENT else StepOutcome.PENDING)
        }
        currentIndex = 0
        val now = System.currentTimeMillis()
        roundStartedAt = now
        stepStartedAt = now
        publish(GameConfig.STEP_DURATION_MS)
        loopJob = viewModelScope.launch {
            while (isActive && !finished) {
                delay(GameConfig.TICK_INTERVAL_MS)
                tick()
            }
        }
    }

    private fun tick() {
        if (finished) return
        val now = System.currentTimeMillis()
        if (now - roundStartedAt >= GameConfig.ROUND_HARD_CAP_MS) {
            finishRound()
            return
        }
        val stepElapsed = now - stepStartedAt
        val remaining = GameConfig.STEP_DURATION_MS - stepElapsed
        if (remaining <= 0L) {
            if (settings.autoAdvance) {
                completeStep(StepOutcome.DONE)
            } else {
                publish(0L)
            }
            return
        }
        publish(remaining)
    }

    fun onDone() {
        if (finished) return
        completeStep(StepOutcome.DONE)
    }

    fun onSkip() {
        if (finished) return
        completeStep(StepOutcome.SKIPPED)
    }

    fun onFinish() {
        if (finished) return
        finishRound()
    }

    private fun completeStep(outcome: StepOutcome) {
        val active = routine ?: return
        if (currentIndex >= outcomes.size) {
            finishRound()
            return
        }
        outcomes[currentIndex] = outcome
        currentIndex++
        if (currentIndex >= active.steps.size) {
            finishRound()
            return
        }
        outcomes[currentIndex] = StepOutcome.CURRENT
        stepStartedAt = System.currentTimeMillis()
        publish(GameConfig.STEP_DURATION_MS)
    }

    private fun finishRound() {
        if (finished) return
        finished = true
        loopJob?.cancel()
        loopJob = null
        val active = routine
        if (active == null) {
            _state.value = _state.value.copy(finishing = true, navigateToResult = true)
            return
        }
        for (index in outcomes.indices) {
            if (outcomes[index] == StepOutcome.CURRENT || outcomes[index] == StepOutcome.PENDING) {
                outcomes[index] = StepOutcome.SKIPPED
            }
        }
        val done = outcomes.count { it == StepOutcome.DONE }
        val skipped = outcomes.count { it == StepOutcome.SKIPPED }
        val duration = System.currentTimeMillis() - roundStartedAt
        val finalRun = RoutineRun(
            routineId = active.id,
            routineName = active.name,
            epochDay = LocalDate.now().toEpochDay(),
            completedSteps = done,
            skippedSteps = skipped,
            totalSteps = active.steps.size,
            durationMillis = duration
        )
        recordRun(finalRun)
        val labels = active.steps.mapIndexed { index, step ->
            Pair(step.name, outcomes.getOrElse(index) { StepOutcome.SKIPPED } == StepOutcome.DONE)
        }
        session.storeResult(finalRun, labels)
        _state.value = _state.value.copy(
            outcomes = outcomes.toList(),
            completedSteps = done,
            skippedSteps = skipped,
            finishing = true,
            navigateToResult = true
        )
    }

    fun consumeNavigation() {
        navigationConsumed = true
        _state.value = _state.value.copy(navigateToResult = false)
    }

    fun navigationHandled(): Boolean = navigationConsumed

    private fun publish(remaining: Long) {
        val active = routine ?: return
        val step = active.steps.getOrNull(currentIndex) ?: return
        val next = active.steps.getOrNull(currentIndex + 1)
        _state.value = GameUiState(
            routineName = active.name,
            stepName = step.name,
            stepDetail = step.detail,
            stepMinutes = step.minutes,
            category = active.category,
            stepNumber = currentIndex + 1,
            totalSteps = active.steps.size,
            remainingMillis = remaining,
            stepDurationMillis = GameConfig.STEP_DURATION_MS,
            outcomes = outcomes.toList(),
            completedSteps = outcomes.count { it == StepOutcome.DONE },
            skippedSteps = outcomes.count { it == StepOutcome.SKIPPED },
            nextStepName = next?.name.orEmpty(),
            finishing = false,
            navigateToResult = false
        )
    }

    override fun onCleared() {
        loopJob?.cancel()
        loopJob = null
        super.onCleared()
    }
}
