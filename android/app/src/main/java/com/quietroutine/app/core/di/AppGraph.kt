package com.quietroutine.app.core.di

import android.content.Context
import com.quietroutine.app.core.session.RunSession
import com.quietroutine.app.data.local.HistoryPreferences
import com.quietroutine.app.data.local.RoutinePreferences
import com.quietroutine.app.data.local.SettingsPreferences
import com.quietroutine.app.data.repository.HistoryRepositoryImpl
import com.quietroutine.app.data.repository.RoutineRepositoryImpl
import com.quietroutine.app.data.repository.SettingsRepositoryImpl
import com.quietroutine.app.domain.repository.HistoryRepository
import com.quietroutine.app.domain.repository.RoutineRepository
import com.quietroutine.app.domain.repository.SettingsRepository
import com.quietroutine.app.domain.usecase.DeleteRoutineUseCase
import com.quietroutine.app.domain.usecase.GetConsistencyStatsUseCase
import com.quietroutine.app.domain.usecase.GetRoutinesUseCase
import com.quietroutine.app.domain.usecase.GetRunHistoryUseCase
import com.quietroutine.app.domain.usecase.RecordRunUseCase
import com.quietroutine.app.domain.usecase.SaveRoutineUseCase

class AppGraph(context: Context) {

    private val settingsPreferences: SettingsPreferences = SettingsPreferences(context)

    val settings: SettingsRepository = SettingsRepositoryImpl(settingsPreferences)

    private val routinePreferences: RoutinePreferences = RoutinePreferences(context)

    private val historyPreferences: HistoryPreferences = HistoryPreferences(context)

    private val routineRepository: RoutineRepository = RoutineRepositoryImpl(routinePreferences)

    private val historyRepository: HistoryRepository = HistoryRepositoryImpl(historyPreferences)

    val session: RunSession = RunSession()

    val getRoutines: GetRoutinesUseCase = GetRoutinesUseCase(routineRepository)

    val saveRoutine: SaveRoutineUseCase = SaveRoutineUseCase(routineRepository)

    val deleteRoutine: DeleteRoutineUseCase = DeleteRoutineUseCase(routineRepository)

    val recordRun: RecordRunUseCase = RecordRunUseCase(historyRepository)

    val consistencyStats: GetConsistencyStatsUseCase = GetConsistencyStatsUseCase(historyRepository)

    val runHistory: GetRunHistoryUseCase = GetRunHistoryUseCase(historyRepository)

    fun history(): HistoryRepository = historyRepository
}
