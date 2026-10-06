package com.quietroutine.app.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quietroutine.app.presentation.game.GameViewModel
import com.quietroutine.app.presentation.gameover.GameOverViewModel
import com.quietroutine.app.presentation.menu.MenuViewModel
import com.quietroutine.app.presentation.splash.SplashViewModel

class ViewModelFactory(private val graph: AppGraph) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                graph.getRoutines,
                graph.consistencyStats,
                graph.runHistory,
                graph.session
            )

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                graph.getRoutines,
                graph.recordRun,
                graph.session,
                graph.settings
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                graph.session,
                graph.consistencyStats
            )

            else -> SplashViewModel()
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
