package com.quietroutine.app.presentation.gameover

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.quietroutine.app.R
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.core.navigation.Navigator
import com.quietroutine.app.core.util.TimeFormatter
import com.quietroutine.app.databinding.FragmentGameoverBinding
import com.quietroutine.app.databinding.ItemSummaryStepBinding
import com.quietroutine.app.presentation.dialog.HistoryDialog
import com.quietroutine.app.presentation.game.GameFragment
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameoverBinding? = null

    private val viewModel: GameOverViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private var ringAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGameoverBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return

        binding.resultRing.max = RING_MAX
        binding.resultAgain.setOnClickListener { runAgain() }
        binding.resultMenu.setOnClickListener { backToMenu() }
        binding.resultHistory.setOnClickListener { openHistory() }

        observeState()
        viewModel.load()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: GameOverUiState) {
        val binding = _binding ?: return
        if (!isAdded) return

        binding.resultTitle.text = getString(
            if (state.perfect) R.string.result_complete else R.string.result_ended
        )
        binding.resultSubtitle.text = state.routineName
        binding.resultRatio.text = getString(
            R.string.result_ratio,
            state.completedSteps,
            state.totalSteps
        )
        ViewCompat.setStateDescription(
            binding.resultRatio,
            getString(if (state.perfect) R.string.state_selected else R.string.state_running)
        )

        animateRing(state.ringTarget)

        binding.resultStatTime.bind(
            TimeFormatter.duration(state.durationMillis),
            getString(R.string.stat_time)
        )
        binding.resultStatSkipped.bindOrHide(
            state.skippedSteps,
            getString(R.string.stat_skipped)
        )
        binding.resultStatStreak.bindOrHide(state.streak, getString(R.string.stat_streak))

        val visible = listOf(
            binding.resultStatTime.isCardVisible(),
            binding.resultStatSkipped.isCardVisible(),
            binding.resultStatStreak.isCardVisible()
        ).count { it }
        binding.resultStats.visibility = if (visible > 1) View.VISIBLE else View.GONE

        renderSteps(state)
    }

    private fun renderSteps(state: GameOverUiState) {
        val binding = _binding ?: return
        binding.resultSteps.removeAllViews()
        if (state.steps.isEmpty()) {
            binding.resultSection.visibility = View.GONE
            return
        }
        binding.resultSection.visibility = View.VISIBLE
        val inflater = LayoutInflater.from(requireContext())
        state.steps.take(MAX_ROWS).forEach { entry ->
            val row = ItemSummaryStepBinding.inflate(inflater, binding.resultSteps, false)
            row.summaryName.text = entry.first
            if (entry.second) {
                row.summaryStatus.setImageResource(R.drawable.ic_check)
                row.summaryStatus.contentDescription = getString(R.string.cd_step_done)
                row.summaryName.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.ink)
                )
            } else {
                row.summaryStatus.setImageResource(R.drawable.ic_skip)
                row.summaryStatus.contentDescription = getString(R.string.cd_step_skipped)
                row.summaryName.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.ink_muted)
                )
            }
            binding.resultSteps.addView(row.root)
        }
    }

    private fun animateRing(target: Int) {
        val binding = _binding ?: return
        ringAnimator?.cancel()
        val animator = ValueAnimator.ofInt(0, target)
        animator.duration = RING_ANIM_MS
        animator.interpolator = DecelerateInterpolator(1.4f)
        animator.addUpdateListener { value ->
            try {
                if (!isAdded) return@addUpdateListener
                val live = _binding ?: return@addUpdateListener
                val progress = value.animatedValue as? Int ?: return@addUpdateListener
                live.resultRing.progress = progress
            } catch (e: Exception) {
            }
        }
        animator.start()
        ringAnimator = animator
    }

    private fun runAgain() {
        if (!isAdded) return
        Navigator.restart(parentFragmentManager, GameFragment(), Navigator.TAG_GAME)
    }

    private fun backToMenu() {
        if (!isAdded) return
        Navigator.backToRoot(parentFragmentManager)
    }

    private fun openHistory() {
        if (!isAdded) return
        HistoryDialog().show(parentFragmentManager, HistoryDialog.TAG)
    }

    override fun onDestroyView() {
        ringAnimator?.cancel()
        ringAnimator = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val RING_MAX = 100
        const val RING_ANIM_MS = 760L
        const val MAX_ROWS = 6
    }
}
