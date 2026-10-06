package com.quietroutine.app.presentation.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.quietroutine.app.R
import com.quietroutine.app.core.ui.CategoryVisuals
import com.quietroutine.app.databinding.ItemRoutineBinding
import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.model.StepOutcome

class RoutineAdapter(
    private val onSelected: (Routine) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.RoutineHolder>() {

    private val items = ArrayList<Routine>()

    private var selectedId: String = ""

    private var consistency: Map<String, Int> = emptyMap()

    fun submit(routines: List<Routine>, selected: String, percents: Map<String, Int>) {
        items.clear()
        items.addAll(routines)
        selectedId = selected
        consistency = percents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineHolder {
        val binding = ItemRoutineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoutineHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RoutineHolder(
        private val binding: ItemRoutineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(routine: Routine) {
            val context = binding.root.context
            binding.routineName.text = routine.name
            binding.routineMeta.text = context.getString(
                R.string.routine_meta,
                routine.stepCount,
                routine.totalMinutes
            )
            val percent = consistency[routine.id] ?: 0
            binding.routineConsistency.text = context.getString(
                R.string.routine_consistency,
                percent
            )
            binding.routineSprite.setImageResource(CategoryVisuals.spriteFor(routine.category))
            binding.routineSprite.contentDescription =
                context.getString(CategoryVisuals.labelFor(routine.category))

            val doneDots = (routine.stepCount * percent) / 100
            val outcomes = ArrayList<StepOutcome>(routine.stepCount)
            for (index in 0 until routine.stepCount) {
                outcomes.add(if (index < doneDots) StepOutcome.DONE else StepOutcome.PENDING)
            }
            binding.routineProgress.setSteps(outcomes, false)

            val selected = routine.id == selectedId
            val stroke = if (selected) R.color.terracotta else R.color.divider
            binding.routineCard.strokeWidth = if (selected) {
                context.resources.getDimensionPixelSize(R.dimen.hairline) * 2
            } else {
                context.resources.getDimensionPixelSize(R.dimen.hairline)
            }
            binding.routineCard.strokeColor = ContextCompat.getColor(context, stroke)
            binding.routineCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (selected) R.color.terracotta_tint else R.color.surface
                )
            )
            ViewCompat.setStateDescription(
                binding.routineCard,
                context.getString(
                    if (selected) R.string.state_selected else R.string.state_not_selected
                )
            )
            binding.routineCard.setOnClickListener { onSelected(routine) }
        }
    }
}
