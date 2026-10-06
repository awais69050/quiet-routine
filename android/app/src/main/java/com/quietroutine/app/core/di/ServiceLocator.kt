package com.quietroutine.app.core.di

import android.content.Context

object ServiceLocator {

    private var graph: AppGraph? = null

    fun init(context: Context): AppGraph = provide(context)

    fun provide(context: Context): AppGraph {
        val existing = graph
        if (existing != null) return existing
        val created = AppGraph(context.applicationContext)
        graph = created
        return created
    }

    fun factory(context: Context): ViewModelFactory = ViewModelFactory(provide(context))
}
