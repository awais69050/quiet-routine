package com.quietroutine.app.presentation.splash

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator

class SplashAnimator {

    private val animators = ArrayList<Animator>()

    private val animatedViews = ArrayList<View>()

    fun spinMark(target: View, reducedMotion: Boolean) {
        if (reducedMotion) return
        val animator = ObjectAnimator.ofFloat(target, View.ROTATION, 0f, 360f)
        animator.duration = MARK_SPIN_MS
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.start()
        animators.add(animator)
    }

    fun breatheLines(targets: List<View>, reducedMotion: Boolean) {
        if (reducedMotion) {
            targets.forEach { it.alpha = LINE_ALPHA_HIGH }
            return
        }
        targets.forEachIndexed { index, target ->
            target.alpha = LINE_ALPHA_LOW
            val animator = ObjectAnimator.ofFloat(
                target,
                View.ALPHA,
                LINE_ALPHA_LOW,
                LINE_ALPHA_HIGH
            )
            animator.duration = LINE_BREATH_MS
            animator.startDelay = index * LINE_STAGGER_MS
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.start()
            animators.add(animator)
        }
    }

    fun enterTitle(title: View, subtitle: View, offsetPx: Float) {
        title.alpha = 0f
        title.translationY = offsetPx
        title.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(TITLE_ENTER_MS)
            .setInterpolator(DecelerateInterpolator(1.6f))
            .start()
        animatedViews.add(title)

        subtitle.alpha = 0f
        subtitle.translationY = offsetPx * 0.6f
        subtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(TITLE_ENTER_MS)
            .setStartDelay(SUBTITLE_DELAY_MS)
            .setInterpolator(DecelerateInterpolator(1.4f))
            .start()
        animatedViews.add(subtitle)
    }

    fun floatMark(target: View, reducedMotion: Boolean, travelPx: Float) {
        if (reducedMotion) return
        val animator = ObjectAnimator.ofFloat(
            target,
            View.TRANSLATION_Y,
            -travelPx,
            travelPx
        )
        animator.duration = MARK_FLOAT_MS
        animator.interpolator = DecelerateInterpolator(1.1f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
        animators.add(animator)
    }

    fun fadeStatus(target: View, reducedMotion: Boolean) {
        if (reducedMotion) {
            target.alpha = 1f
            return
        }
        val animator = ObjectAnimator.ofFloat(target, View.ALPHA, 0.45f, 1f)
        animator.duration = STATUS_FADE_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
        animators.add(animator)
    }

    fun cancelAll() {
        animators.forEach { it.cancel() }
        animators.clear()
        animatedViews.forEach { it.animate().cancel() }
        animatedViews.clear()
    }

    private companion object {
        const val MARK_SPIN_MS = 9000L
        const val MARK_FLOAT_MS = 2600L
        const val LINE_BREATH_MS = 1600L
        const val LINE_STAGGER_MS = 260L
        const val TITLE_ENTER_MS = 620L
        const val SUBTITLE_DELAY_MS = 180L
        const val STATUS_FADE_MS = 1400L
        const val LINE_ALPHA_LOW = 0.25f
        const val LINE_ALPHA_HIGH = 0.75f
    }
}
