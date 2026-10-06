package com.quietroutine.app.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.quietroutine.app.R
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.databinding.DialogAddStepBinding
import com.quietroutine.app.databinding.DialogRoutineBuilderBinding
import com.quietroutine.app.domain.model.Routine
import com.quietroutine.app.domain.model.RoutineCategory
import com.quietroutine.app.domain.model.RoutineStep

class RoutineBuilderDialog : DialogFragment() {

    private var _binding: DialogRoutineBuilderBinding? = null

    private val steps = ArrayList<RoutineStep>()

    private var adapter: StepAdapter? = null

    private var category = RoutineCategory.MORNING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_App_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogRoutineBuilderBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return

        val created = StepAdapter(
            onMoveUp = { index -> moveStep(index, -1) },
            onMoveDown = { index -> moveStep(index, 1) },
            onRemove = { index -> removeStep(index) }
        )
        adapter = created
        binding.builderSteps.layoutManager = LinearLayoutManager(requireContext())
        binding.builderSteps.adapter = created

        binding.builderClose.setOnClickListener { dismissAllowingStateLoss() }
        binding.builderCancel.setOnClickListener { dismissAllowingStateLoss() }
        binding.builderAddStep.setOnClickListener { askForStep() }
        binding.builderSave.setOnClickListener { saveRoutine() }

        binding.builderCategories.check(R.id.cat_morning)
        binding.builderCategories.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            binding.builderCategoriesTwo.clearChecked()
            category = when (checkedId) {
                R.id.cat_evening -> RoutineCategory.EVENING
                R.id.cat_focus -> RoutineCategory.FOCUS
                else -> RoutineCategory.MORNING
            }
        }
        binding.builderCategoriesTwo.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            binding.builderCategories.clearChecked()
            category = when (checkedId) {
                R.id.cat_calm -> RoutineCategory.CALM
                R.id.cat_care -> RoutineCategory.CARE
                else -> RoutineCategory.MOVE
            }
        }

        renderSteps()
    }

    private fun askForStep() {
        if (!isAdded) return
        val inflater = LayoutInflater.from(requireContext())
        val content = DialogAddStepBinding.inflate(inflater, null, false)
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_Alert)
            .setTitle(R.string.builder_add_step)
            .setView(content.root)
            .setPositiveButton(R.string.builder_add_step) { dialog, _ ->
                val name = content.addStepInput.text?.toString().orEmpty().trim()
                if (name.isNotEmpty()) addStep(name)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cta_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun addStep(name: String) {
        val minutes = ServiceLocator.provide(requireContext()).settings.defaultStepMinutes
        steps.add(
            RoutineStep(
                id = "step_" + System.currentTimeMillis(),
                name = name,
                detail = getString(R.string.builder_step_detail),
                minutes = minutes
            )
        )
        renderSteps()
    }

    private fun moveStep(index: Int, direction: Int) {
        val target = index + direction
        if (index !in steps.indices) return
        if (target !in steps.indices) return
        val moved = steps.removeAt(index)
        steps.add(target, moved)
        renderSteps()
    }

    private fun removeStep(index: Int) {
        if (index !in steps.indices) return
        steps.removeAt(index)
        renderSteps()
    }

    private fun renderSteps() {
        val binding = _binding ?: return
        adapter?.submit(steps.toList())
        binding.builderEmpty.visibility = if (steps.isEmpty()) View.VISIBLE else View.GONE
        binding.builderSteps.visibility = if (steps.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun saveRoutine() {
        val binding = _binding ?: return
        val name = binding.builderNameInput.text?.toString().orEmpty().trim()
        if (name.isEmpty()) {
            showError(getString(R.string.builder_name_error))
            return
        }
        if (steps.isEmpty()) {
            showError(getString(R.string.builder_steps_error))
            return
        }
        val routine = Routine(
            id = "routine_" + System.currentTimeMillis(),
            name = name,
            category = category,
            steps = steps.toList()
        )
        val saved = ServiceLocator.provide(requireContext()).saveRoutine(routine)
        if (saved) {
            dismissAllowingStateLoss()
        } else {
            showError(getString(R.string.builder_steps_error))
        }
    }

    private fun showError(message: String) {
        val binding = _binding ?: return
        binding.builderError.text = message
        binding.builderError.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        val binding = _binding
        if (binding != null) {
            binding.builderSteps.adapter = null
        }
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "routine_builder"
    }
}
