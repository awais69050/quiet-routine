package com.quietroutine.app.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quietroutine.app.R
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.core.util.TimeFormatter
import com.quietroutine.app.databinding.DialogHistoryBinding
import com.quietroutine.app.databinding.ItemHistoryBarBinding

class HistoryDialog : DialogFragment() {

    private var _binding: DialogHistoryBinding? = null

    private var adapter: HistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_App_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogHistoryBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return

        val created = HistoryAdapter()
        adapter = created
        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyList.adapter = created
        binding.historyClose.setOnClickListener { dismissAllowingStateLoss() }

        val graph = ServiceLocator.provide(requireContext())
        val runs = graph.runHistory()
        val stats = graph.consistencyStats()

        created.submit(runs)
        binding.historyEmpty.visibility = if (runs.isEmpty()) View.VISIBLE else View.GONE
        binding.historyList.visibility = if (runs.isEmpty()) View.GONE else View.VISIBLE

        binding.historyStatBest.bindOrHide(stats.bestStreak, getString(R.string.stat_best_streak))
        binding.historyStatAvg.bindOrHide(
            stats.averageCompletionPercent,
            getString(R.string.stat_avg_done)
        )
        binding.historyStatRuns.bindOrHide(stats.totalRuns, getString(R.string.stat_runs))

        val visible = listOf(
            binding.historyStatBest.isCardVisible(),
            binding.historyStatAvg.isCardVisible(),
            binding.historyStatRuns.isCardVisible()
        ).count { it }
        binding.historyStats.visibility = if (visible > 1) View.VISIBLE else View.GONE

        renderBars(stats.dayCompletion, graph.runHistory.today())
    }

    private fun renderBars(values: List<Float>, today: Long) {
        val binding = _binding ?: return
        binding.historyBars.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val maxHeight = resources.getDimensionPixelSize(R.dimen.history_bar_height)
        val minHeight = resources.getDimensionPixelSize(R.dimen.gap_xs) / 2
        val gap = resources.getDimensionPixelSize(R.dimen.gap_xs)
        values.forEachIndexed { index, value ->
            val row = ItemHistoryBarBinding.inflate(inflater, binding.historyBars, false)
            val height = when {
                value <= 0f -> minHeight
                else -> (maxHeight * value).toInt().coerceAtLeast(minHeight * 2)
            }
            val params = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.history_bar_width),
                height
            )
            row.barFill.layoutParams = params
            row.barFill.setBackgroundResource(
                when {
                    value <= 0f -> R.drawable.bar_empty
                    value >= 0.99f -> R.drawable.bar_full
                    else -> R.drawable.bar_partial
                }
            )
            val epochDay = today - (values.size - 1 - index)
            row.barLabel.text = TimeFormatter.dayLabel(epochDay)
            val rowParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (index > 0) rowParams.marginStart = gap
            row.root.layoutParams = rowParams
            binding.historyBars.addView(row.root)
        }
    }

    override fun onDestroyView() {
        val binding = _binding
        if (binding != null) {
            binding.historyList.adapter = null
        }
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "history_dialog"
    }
}
