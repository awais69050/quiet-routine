package com.quietroutine.app.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quietroutine.app.R
import com.quietroutine.app.core.util.TimeFormatter
import com.quietroutine.app.databinding.ItemHistoryBinding
import com.quietroutine.app.domain.model.RoutineRun

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.RunHolder>() {

    private val items = ArrayList<RoutineRun>()

    fun submit(runs: List<RoutineRun>) {
        items.clear()
        items.addAll(runs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RunHolder(binding)
    }

    override fun onBindViewHolder(holder: RunHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RunHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(run: RoutineRun) {
            val context = binding.root.context
            binding.historyDate.text = TimeFormatter.dateLabel(run.epochDay)
            binding.historyRoutine.text = run.routineName
            binding.historySummary.text = context.getString(
                R.string.history_run_summary,
                run.completedSteps,
                run.totalSteps,
                TimeFormatter.duration(run.durationMillis)
            )
            if (run.skippedSteps == 0) {
                binding.historyStatus.setImageResource(R.drawable.ic_check)
                binding.historyStatus.contentDescription =
                    context.getString(R.string.cd_step_done)
            } else {
                binding.historyStatus.setImageResource(R.drawable.ic_skip)
                binding.historyStatus.contentDescription =
                    context.getString(R.string.cd_step_skipped)
            }
        }
    }
}
