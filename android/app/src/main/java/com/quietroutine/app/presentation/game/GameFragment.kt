package com.quietroutine.app.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.quietroutine.app.R
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.core.navigation.Navigator
import com.quietroutine.app.core.ui.CategoryVisuals
import com.quietroutine.app.core.util.TimeFormatter
import com.quietroutine.app.databinding.FragmentGameBinding
import com.quietroutine.app.presentation.gameover.GameOverFragment
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null

    private val viewModel: GameViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private var reducedMotion = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGameBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return
        reducedMotion = ServiceLocator.provide(requireContext()).settings.reducedMotion

        binding.gameBack.setOnClickListener { goBack() }
        binding.gameDone.setOnClickListener {
            pulseCard()
            viewModel.onDone()
        }
        binding.gameSkip.setOnClickListener { viewModel.onSkip() }
        binding.gameFinish.setOnClickListener { viewModel.onFinish() }

        binding.gameTimerRing.max = RING_MAX

        observeState()
        viewModel.start()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: GameUiState) {
        val binding = _binding ?: return
        if (!isAdded) return

        if (state.totalSteps > 0) {
            binding.gameTitle.text = state.routineName
            binding.gameCounter.text = getString(
                R.string.game_step_counter,
                state.stepNumber,
                state.totalSteps
            )
            binding.gameStepName.text = state.stepName
            binding.gameStepDetail.text = getString(
                R.string.step_meta,
                state.stepMinutes,
                state.stepDetail
            )
            binding.gameSprite.setImageResource(CategoryVisuals.spriteFor(state.category))
            binding.gameSprite.contentDescription =
                getString(CategoryVisuals.labelFor(state.category))
            binding.gameTimerText.text = TimeFormatter.clock(state.remainingMillis)
            binding.gameTimerRing.progress = state.ringProgress
            binding.gameProgress.setSteps(state.outcomes, !reducedMotion)
            binding.gameNext.text = if (state.hasNextStep) {
                getString(R.string.game_next, state.nextStepName.uppercase())
            } else {
                getString(R.string.game_next_summary)
            }
            ViewCompat.setStateDescription(
                binding.gameCounter,
                getString(if (state.finishing) R.string.state_finishing else R.string.state_running)
            )
        }

        val enabled = !state.finishing
        binding.gameDone.isEnabled = enabled
        binding.gameSkip.isEnabled = enabled
        binding.gameFinish.isEnabled = enabled
        val alpha = if (enabled) 1f else DISABLED_ALPHA
        binding.gameDone.alpha = alpha
        binding.gameSkip.alpha = alpha
        binding.gameFinish.alpha = alpha

        if (state.navigateToResult) {
            showResult()
        }
    }

    private fun pulseCard() {
        if (reducedMotion) return
        val binding = _binding ?: return
        binding.gameStepCard.animate().cancel()
        binding.gameStepCard.scaleX = 0.97f
        binding.gameStepCard.scaleY = 0.97f
        binding.gameStepCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(PULSE_MS)
            .start()
    }

    private fun showResult() {
        if (!isAdded) return
        if (viewModel.navigationHandled()) return
        viewModel.consumeNavigation()
        Navigator.pushResult(
            parentFragmentManager,
            GameOverFragment(),
            Navigator.TAG_RESULT
        )
    }

    private fun goBack() {
        if (!isAdded) return
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        val binding = _binding
        if (binding != null) {
            binding.gameStepCard.animate().cancel()
            binding.gameProgress.stopAnimation()
        }
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val RING_MAX = 100
        const val PULSE_MS = 260L
        const val DISABLED_ALPHA = 0.45f
    }
}
