package com.quietroutine.app.data.repository

import com.quietroutine.app.data.local.RoutinePreferences
import com.quietroutine.app.data.sample.SampleData
import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.repository.RoutineRepository

class RoutineRepositoryImpl(
    private val preferences: RoutinePreferences
) : RoutineRepository {

    private val cache = ArrayList<Routine>()

    init {
        if (!preferences.hasData()) {
            cache.addAll(SampleData.routines())
            preferences.save(cache)
        } else {
            val stored = preferences.load()
            if (stored.isEmpty()) {
                cache.addAll(SampleData.routines())
                preferences.save(cache)
            } else {
                cache.addAll(stored)
            }
        }
    }

    override fun routines(): List<Routine> = cache.toList()

    override fun routineById(id: String): Routine? = cache.firstOrNull { it.id == id }

    override fun save(routine: Routine) {
        val index = cache.indexOfFirst { it.id == routine.id }
        if (index >= 0) {
            cache[index] = routine
        } else {
            cache.add(0, routine)
        }
        preferences.save(cache)
    }

    override fun delete(id: String) {
        val index = cache.indexOfFirst { it.id == id }
        if (index >= 0) {
            cache.removeAt(index)
            preferences.save(cache)
        }
    }
}
