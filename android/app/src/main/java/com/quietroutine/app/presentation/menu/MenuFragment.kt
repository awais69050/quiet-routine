package com.quietroutine.app.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quietroutine.app.R
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.core.navigation.Navigator
import com.quietroutine.app.databinding.FragmentMenuBinding
import com.quietroutine.app.presentation.dialog.HistoryDialog
import com.quietroutine.app.presentation.dialog.RoutineBuilderDialog
import com.quietroutine.app.presentation.dialog.SettingsDialog
import com.quietroutine.app.presentation.game.GameFragment
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    private val viewModel: MenuViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private var adapter: RoutineAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMenuBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return

        val created = RoutineAdapter { routine -> viewModel.select(routine.id) }
        adapter = created
        binding.menuList.layoutManager = LinearLayoutManager(requireContext())
        binding.menuList.adapter = created

        binding.menuCta.setOnClickListener { startSelectedRoutine() }
        binding.menuNewRoutine.setOnClickListener { openBuilder() }
        binding.menuHistory.setOnClickListener { openHistory() }
        binding.menuSettings.setOnClickListener { openSettings() }

        playEntrance()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val binding = _binding ?: return
        if (!isAdded) return
        adapter?.submit(state.routines, state.selectedId, state.consistency)

        binding.statStreak.bindOrHide(state.streak, getString(R.string.stat_streak))
        binding.statDoneToday.bindOrHide(state.doneToday, getString(R.string.stat_done_today))
        binding.statRoutines.bindOrHide(state.routineCount, getString(R.string.stat_routines))

        val visible = listOf(
            binding.statStreak.isCardVisible(),
            binding.statDoneToday.isCardVisible(),
            binding.statRoutines.isCardVisible()
        ).count { it }
        binding.menuStats.visibility = if (visible > 1) View.VISIBLE else View.GONE

        binding.menuCta.isEnabled = state.hasRoutines
        binding.menuCta.alpha = if (state.hasRoutines) 1f else DISABLED_ALPHA
    }

    private fun playEntrance() {
        val binding = _binding ?: return
        binding.menuTitleTop.alpha = 0f
        binding.menuTitleTop.translationY = ENTRANCE_OFFSET
        binding.menuTitleTop.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(ENTRANCE_MS)
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()

        binding.menuTitleBottom.alpha = 0f
        binding.menuTitleBottom.translationY = ENTRANCE_OFFSET
        binding.menuTitleBottom.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(ENTRANCE_MS)
            .setStartDelay(80L)
            .setInterpolator(DecelerateInterpolator(1.5f))
            .start()

        binding.menuCta.alpha = 0f
        binding.menuCta.animate()
            .alpha(1f)
            .setDuration(CTA_ENTRANCE_MS)
            .setStartDelay(180L)
            .start()
    }

    private fun startSelectedRoutine() {
        if (!isAdded) return
        val selected = viewModel.selectedRoutineId()
        if (selected.isEmpty()) return
        Navigator.push(parentFragmentManager, GameFragment(), Navigator.TAG_GAME)
    }

    private fun openBuilder() {
        if (!isAdded) return
        RoutineBuilderDialog().show(parentFragmentManager, RoutineBuilderDialog.TAG)
    }

    private fun openHistory() {
        if (!isAdded) return
        HistoryDialog().show(parentFragmentManager, HistoryDialog.TAG)
    }

    private fun openSettings() {
        if (!isAdded) return
        SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
    }

    override fun onDestroyView() {
        val binding = _binding
        if (binding != null) {
            binding.menuTitleTop.animate().cancel()
            binding.menuTitleBottom.animate().cancel()
            binding.menuCta.animate().cancel()
            binding.menuList.adapter = null
        }
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ENTRANCE_MS = 380L
        const val CTA_ENTRANCE_MS = 420L
        const val ENTRANCE_OFFSET = 16f
        const val DISABLED_ALPHA = 0.45f
    }
}
