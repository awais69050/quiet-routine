package com.quietroutine.app.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quietroutine.app.R
import com.quietroutine.app.databinding.ItemBuilderStepBinding
import com.quietroutine.app.domain.model.RoutineStep

class StepAdapter(
    private val onMoveUp: (Int) -> Unit,
    private val onMoveDown: (Int) -> Unit,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<StepAdapter.StepHolder>() {

    private val items = ArrayList<RoutineStep>()

    fun submit(steps: List<RoutineStep>) {
        items.clear()
        items.addAll(steps)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepHolder {
        val binding = ItemBuilderStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepHolder(binding)
    }

    override fun onBindViewHolder(holder: StepHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StepHolder(
        private val binding: ItemBuilderStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: RoutineStep) {
            val context = binding.root.context
            binding.builderStepName.text = step.name
            binding.builderStepMeta.text = context.getString(
                R.string.step_meta,
                step.minutes,
                step.detail
            )
            binding.builderStepUp.setOnClickListener {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onMoveUp(index)
            }
            binding.builderStepDown.setOnClickListener {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onMoveDown(index)
            }
            binding.builderStepRemove.setOnClickListener {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onRemove(index)
            }
        }
    }
}
