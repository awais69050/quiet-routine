package com.quietroutine.app.data.sample

import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.model.RoutineCategory
import com.quietroutine.app.domain.model.RoutineRun
import com.quietroutine.app.domain.model.RoutineStep

object SampleData {

    fun routines(): List<Routine> = listOf(
        Routine(
            id = "morning_reset",
            name = "Morning Reset",
            category = RoutineCategory.MORNING,
            steps = listOf(
                RoutineStep("mr_1", "Drink a glass of water", "Start before the kettle", 1),
                RoutineStep("mr_2", "Make the bed", "Tidy surface, calm room", 2),
                RoutineStep("mr_3", "Open the window", "Two minutes of fresh air", 1),
                RoutineStep("mr_4", "Stretch the back", "Slow, no rush", 5),
                RoutineStep("mr_5", "Plan three tasks", "Only three, nothing more", 5),
                RoutineStep("mr_6", "Clear the desk", "One clean surface", 4)
            )
        ),
        Routine(
            id = "evening_wind_down",
            name = "Evening Wind Down",
            category = RoutineCategory.EVENING,
            steps = listOf(
                RoutineStep("ev_1", "Dim the lights", "Warm lamps only", 1),
                RoutineStep("ev_2", "Tidy the kitchen", "Counters and sink", 10),
                RoutineStep("ev_3", "Lay out tomorrow", "Clothes and bag ready", 2),
                RoutineStep("ev_4", "Read one chapter", "Paper, not a screen", 10),
                RoutineStep("ev_5", "Phone on the shelf", "Out of arm's reach", 1)
            )
        ),
        Routine(
            id = "focus_block",
            name = "Focus Block",
            category = RoutineCategory.FOCUS,
            steps = listOf(
                RoutineStep("fb_1", "Close every tab", "Keep one window", 2),
                RoutineStep("fb_2", "Silence the phone", "Do not disturb on", 1),
                RoutineStep("fb_3", "Work one task", "No switching allowed", 25),
                RoutineStep("fb_4", "Note where you stopped", "One line is enough", 2)
            )
        ),
        Routine(
            id = "desk_break",
            name = "Desk Break",
            category = RoutineCategory.MOVE,
            steps = listOf(
                RoutineStep("db_1", "Stand and roll shoulders", "Slow circles both ways", 2),
                RoutineStep("db_2", "Look out the window", "Let the eyes rest", 1),
                RoutineStep("db_3", "Refill the cup", "Water, then tea", 2),
                RoutineStep("db_4", "Walk one lap", "Around the room is fine", 5)
            )
        ),
        Routine(
            id = "calm_corner",
            name = "Calm Corner",
            category = RoutineCategory.CALM,
            steps = listOf(
                RoutineStep("cc_1", "Light the candle", "One small flame", 1),
                RoutineStep("cc_2", "Breathe in fours", "Four in, four out", 4),
                RoutineStep("cc_3", "Write one thought", "Whatever is loudest", 3)
            )
        ),
        Routine(
            id = "care_hour",
            name = "Care Hour",
            category = RoutineCategory.CARE,
            steps = listOf(
                RoutineStep("ch_1", "Brew something warm", "Kettle on, mug out", 5),
                RoutineStep("ch_2", "Water the plants", "Check the dry soil", 4),
                RoutineStep("ch_3", "Message one person", "Short note, no pressure", 3)
            )
        )
    )

    fun seedRuns(todayEpochDay: Long): List<RoutineRun> {
        val seeds = listOf(
            SeedRun(1L, "morning_reset", "Morning Reset", 6, 0, 6, 735000L),
            SeedRun(2L, "focus_block", "Focus Block", 3, 1, 4, 512000L),
            SeedRun(3L, "evening_wind_down", "Evening Wind Down", 5, 0, 5, 648000L),
            SeedRun(4L, "morning_reset", "Morning Reset", 5, 1, 6, 702000L),
            SeedRun(5L, "desk_break", "Desk Break", 4, 0, 4, 388000L),
            SeedRun(6L, "morning_reset", "Morning Reset", 4, 2, 6, 690000L),
            SeedRun(8L, "calm_corner", "Calm Corner", 3, 0, 3, 294000L),
            SeedRun(9L, "evening_wind_down", "Evening Wind Down", 4, 1, 5, 601000L),
            SeedRun(11L, "care_hour", "Care Hour", 3, 0, 3, 310000L)
        )
        return seeds.map { seed ->
            RoutineRun(
                routineId = seed.routineId,
                routineName = seed.routineName,
                epochDay = todayEpochDay - seed.daysAgo,
                completedSteps = seed.completed,
                skippedSteps = seed.skipped,
                totalSteps = seed.total,
                durationMillis = seed.durationMillis
            )
        }
    }

    private data class SeedRun(
        val daysAgo: Long,
        val routineId: String,
        val routineName: String,
        val completed: Int,
        val skipped: Int,
        val total: Int,
        val durationMillis: Long
    )
}
