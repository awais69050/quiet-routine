package com.quietroutine.app.data.local

import android.content.Context
import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.model.RoutineCategory
import com.quietroutine.app.domain.model.RoutineStep

class RoutinePreferences(context: Context) {

    private val prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun hasData(): Boolean = prefs.contains(KEY_ROUTINES)

    fun load(): List<Routine> {
        val raw = prefs.getString(KEY_ROUTINES, "").orEmpty()
        if (raw.isEmpty()) return emptyList()
        return raw.split(ROUTINE_SEP)
            .filter { it.isNotBlank() }
            .mapNotNull { decodeRoutine(it) }
    }

    fun save(routines: List<Routine>) {
        val encoded = routines.joinToString(ROUTINE_SEP) { encodeRoutine(it) }
        prefs.edit().putString(KEY_ROUTINES, encoded).apply()
    }

    private fun encodeRoutine(routine: Routine): String {
        val steps = routine.steps.joinToString(STEP_SEP) { step ->
            listOf(
                clean(step.id),
                clean(step.name),
                clean(step.detail),
                step.minutes.toString()
            ).joinToString(FIELD_SEP)
        }
        return listOf(
            clean(routine.id),
            clean(routine.name),
            routine.category.name,
            steps
        ).joinToString(BLOCK_SEP)
    }

    private fun decodeRoutine(raw: String): Routine? {
        val parts = raw.split(BLOCK_SEP)
        if (parts.size < 4) return null
        val category = RoutineCategory.values()
            .firstOrNull { it.name == parts[2] } ?: RoutineCategory.MORNING
        val steps = parts[3].split(STEP_SEP)
            .filter { it.isNotBlank() }
            .mapNotNull { decodeStep(it) }
        if (steps.isEmpty()) return null
        return Routine(
            id = parts[0],
            name = parts[1],
            category = category,
            steps = steps
        )
    }

    private fun decodeStep(raw: String): RoutineStep? {
        val parts = raw.split(FIELD_SEP)
        if (parts.size < 4) return null
        val minutes = parts[3].toIntOrNull() ?: 1
        return RoutineStep(
            id = parts[0],
            name = parts[1],
            detail = parts[2],
            minutes = minutes
        )
    }

    private fun clean(value: String): String = value
        .replace(ROUTINE_SEP, " ")
        .replace(BLOCK_SEP, " ")
        .replace(STEP_SEP, " ")
        .replace(FIELD_SEP, " ")
        .trim()

    companion object {
        private const val FILE_NAME = "quiet_routine_routines"
        private const val KEY_ROUTINES = "routines"
        private const val ROUTINE_SEP = ";"
        private const val BLOCK_SEP = "|"
        private const val STEP_SEP = "~"
        private const val FIELD_SEP = "^"
    }
}
