package com.quietroutine.app.domain.model

data class RoutineStep(
    val id: String,
    val name: String,
    val detail: String,
    val minutes: Int
)
