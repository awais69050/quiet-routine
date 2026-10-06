package com.quietroutine.app.data.local

import android.content.Context
import com.quietroutine.app.core.config.GameConfig
import com.quietroutine.app.domain.model.RoutineRun

class HistoryPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun hasData(): Boolean = prefs.contains(KEY_RUNS)

    fun load(): List<RoutineRun> {
        val raw = prefs.getString(KEY_RUNS, "").orEmpty()
        if (raw.isEmpty()) return emptyList()
        return raw.split(RUN_SEP)
            .filter { it.isNotBlank() }
            .mapNotNull { decode(it) }
    }

    fun save(runs: List<RoutineRun>) {
        val trimmed = runs.take(GameConfig.HISTORY_LIMIT)
        val encoded = trimmed.joinToString(RUN_SEP) { encode(it) }
        prefs.edit().putString(KEY_RUNS, encoded).apply()
    }

    fun clear() {
        prefs.edit().putString(KEY_RUNS, "").apply()
    }

    private fun encode(run: RoutineRun): String = listOf(
        clean(run.routineId),
        clean(run.routineName),
        run.epochDay.toString(),
        run.completedSteps.toString(),
        run.skippedSteps.toString(),
        run.totalSteps.toString(),
        run.durationMillis.toString()
    ).joinToString(FIELD_SEP)

    private fun decode(raw: String): RoutineRun? {
        val parts = raw.split(FIELD_SEP)
        if (parts.size < 7) return null
        val epochDay = parts[2].toLongOrNull() ?: return null
        val completed = parts[3].toIntOrNull() ?: return null
        val skipped = parts[4].toIntOrNull() ?: return null
        val total = parts[5].toIntOrNull() ?: return null
        val duration = parts[6].toLongOrNull() ?: 0L
        return RoutineRun(
            routineId = parts[0],
            routineName = parts[1],
            epochDay = epochDay,
            completedSteps = completed,
            skippedSteps = skipped,
            totalSteps = total,
            durationMillis = duration
        )
    }

    private fun clean(value: String): String = value
        .replace(RUN_SEP, " ")
        .replace(FIELD_SEP, " ")
        .trim()

    companion object {
        private const val FILE_NAME = "quiet_routine_history"
        private const val KEY_RUNS = "runs"
        private const val RUN_SEP = ";"
        private const val FIELD_SEP = "|"
    }
}
