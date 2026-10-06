package com.quietroutine.app.data.repository

import com.quietroutine.app.core.config.GameConfig
import com.quietroutine.app.data.local.HistoryPreferences
import com.quietroutine.app.data.sample.SampleData
import com.quietroutine.app.domain.model.RoutineRun
import com.quietroutine.app.domain.repository.HistoryRepository
import java.time.LocalDate

class HistoryRepositoryImpl(
    private val preferences: HistoryPreferences
) : HistoryRepository {

    private val cache = ArrayList<RoutineRun>()

    init {
        if (!preferences.hasData()) {
            cache.addAll(SampleData.seedRuns(todayEpochDay()))
            preferences.save(cache)
        } else {
            cache.addAll(preferences.load())
        }
    }

    override fun runs(): List<RoutineRun> = cache.toList()

    override fun record(run: RoutineRun) {
        cache.add(0, run)
        while (cache.size > GameConfig.HISTORY_LIMIT) {
            cache.removeAt(cache.size - 1)
        }
        preferences.save(cache)
    }

    override fun lastRun(): RoutineRun? = cache.firstOrNull()

    override fun clear() {
        cache.clear()
        preferences.clear()
    }

    override fun todayEpochDay(): Long = LocalDate.now().toEpochDay()
}
