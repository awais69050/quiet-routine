package com.quietroutine.app.core.ui

import com.quietroutine.app.R
import com.quietroutine.app.domain.model.RoutineCategory

object CategoryVisuals {

    fun spriteFor(category: RoutineCategory): Int = when (category) {
        RoutineCategory.MORNING -> R.drawable.sprite_sunrise
        RoutineCategory.EVENING -> R.drawable.sprite_moon
        RoutineCategory.FOCUS -> R.drawable.sprite_leaf
        RoutineCategory.MOVE -> R.drawable.sprite_stretch
        RoutineCategory.CALM -> R.drawable.sprite_candle
        RoutineCategory.CARE -> R.drawable.sprite_cup
    }

    fun labelFor(category: RoutineCategory): Int = when (category) {
        RoutineCategory.MORNING -> R.string.cat_morning
        RoutineCategory.EVENING -> R.string.cat_evening
        RoutineCategory.FOCUS -> R.string.cat_focus
        RoutineCategory.MOVE -> R.string.cat_move
        RoutineCategory.CALM -> R.string.cat_calm
        RoutineCategory.CARE -> R.string.cat_care
    }
}
