package com.quietroutine.app.core.util

import java.util.Locale

object TimeFormatter {

    fun clock(millis: Long): String {
        val safe = if (millis < 0L) 0L else millis
        val totalSeconds = (safe + 999L) / 1000L
        val minutes = totalSeconds / 60L
        val seconds = totalSeconds % 60L
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun duration(millis: Long): String = clock(millis)

    fun dayLabel(epochDay: Long): String {
        val index = ((epochDay % 7L) + 7L) % 7L
        return DAY_LETTERS[index.toInt()]
    }

    fun dateLabel(epochDay: Long): String {
        val date = java.time.LocalDate.ofEpochDay(epochDay)
        val day = date.dayOfWeek.name.take(3)
        val month = date.month.name.take(3)
        return String.format(Locale.US, "%s %d %s", day, date.dayOfMonth, month)
    }

    private val DAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")
}
