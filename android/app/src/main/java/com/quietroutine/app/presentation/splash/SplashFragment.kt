package com.quietroutine.app.presentation.splash

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
import com.quietroutine.app.databinding.FragmentSplashBinding
import com.quietroutine.app.presentation.menu.MenuFragment
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null

    private val viewModel: SplashViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private val animator = SplashAnimator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSplashBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return
        val reducedMotion = ServiceLocator.provide(requireContext()).settings.reducedMotion

        ViewCompat.setStateDescription(
            binding.splashProgress,
            getString(R.string.state_running)
        )

        animator.enterTitle(
            binding.splashTitle,
            binding.splashSubtitle,
            resources.getDimensionPixelSize(R.dimen.gap_m).toFloat()
        )
        animator.spinMark(binding.splashMark, reducedMotion)
        animator.breatheLines(
            listOf(
                binding.splashLineOne,
                binding.splashLineTwo,
                binding.splashLineThree
            ),
            reducedMotion
        )
        animator.floatMark(
            binding.splashLines,
            reducedMotion,
            resources.getDimensionPixelSize(R.dimen.gap_xs).toFloat()
        )
        animator.fadeStatus(binding.splashStatus, reducedMotion)

        observeState()
        viewModel.start()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: SplashUiState) {
        val binding = _binding ?: return
        if (!isAdded) return
        if (!state.preparing) {
            binding.splashStatus.alpha = 1f
        }
        if (state.readyToAdvance) {
            handOver()
        }
    }

    private fun handOver() {
        if (!isAdded) return
        if (viewModel.hasHandedOver()) return
        viewModel.consumeAdvance()
        animator.cancelAll()
        Navigator.showRoot(
            parentFragmentManager,
            MenuFragment(),
            Navigator.TAG_MENU
        )
    }

    override fun onDestroyView() {
        animator.cancelAll()
        _binding = null
        super.onDestroyView()
    }
}
