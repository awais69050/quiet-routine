package com.quietroutine.app.core.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.quietroutine.app.R
import com.quietroutine.app.core.config.GameConfig
import com.quietroutine.app.databinding.ViewStepProgressBinding
import com.quietroutine.app.domain.model.StepOutcome

class StepProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStepProgressBinding.inflate(LayoutInflater.from(context), this)

    private var pulse: ObjectAnimator? = null

    private val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)

    private val dotGap = resources.getDimensionPixelSize(R.dimen.dot_gap)

    fun setSteps(outcomes: List<StepOutcome>, animateCurrent: Boolean) {
        stopAnimation()
        binding.dotRow.removeAllViews()
        outcomes.forEachIndexed { index, outcome ->
            val dot = View(context)
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            if (index > 0) params.marginStart = dotGap
            dot.layoutParams = params
            dot.setBackgroundResource(backgroundFor(outcome))
            binding.dotRow.addView(dot)
            if (outcome == StepOutcome.CURRENT && animateCurrent) {
                startPulse(dot)
            }
        }
    }

    fun stopAnimation() {
        pulse?.cancel()
        pulse = null
    }

    private fun startPulse(target: View) {
        val animator = ObjectAnimator.ofFloat(target, View.ALPHA, 0.55f, 1f)
        animator.duration = GameConfig.PULSE_ANIM_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()
        pulse = animator
    }

    private fun backgroundFor(outcome: StepOutcome): Int = when (outcome) {
        StepOutcome.DONE -> R.drawable.dot_done
        StepOutcome.CURRENT -> R.drawable.dot_current
        StepOutcome.SKIPPED -> R.drawable.dot_skipped
        StepOutcome.PENDING -> R.drawable.dot_pending
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }
}
