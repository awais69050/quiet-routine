package com.quietroutine.app.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.quietroutine.app.R

object Navigator {

    const val TAG_SPLASH = "splash"
    const val TAG_MENU = "menu"
    const val TAG_GAME = "game"
    const val TAG_RESULT = "result"

    fun showRoot(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun push(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun pushResult(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.slide_out_up,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun backToRoot(manager: FragmentManager) {
        if (manager.isStateSaved) return
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun restart(manager: FragmentManager, fragment: Fragment, tag: String) {
        if (manager.isStateSaved) return
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        push(manager, fragment, tag)
    }
}
