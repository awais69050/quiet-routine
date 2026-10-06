package com.quietroutine.app.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.quietroutine.app.R
import com.quietroutine.app.core.di.ServiceLocator
import com.quietroutine.app.databinding.DialogSettingsBinding
import com.quietroutine.app.domain.repository.SettingsRepository

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null

    private var settings: SettingsRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_App_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogSettingsBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = _binding ?: return
        val repository = ServiceLocator.provide(requireContext()).settings
        settings = repository

        binding.settingsClose.setOnClickListener { dismissAllowingStateLoss() }

        binding.settingsAutoAdvance.isChecked = repository.autoAdvance
        binding.settingsKeepAwake.isChecked = repository.keepAwake
        binding.settingsReducedMotion.isChecked = repository.reducedMotion
        describe(binding.settingsAutoAdvance, repository.autoAdvance)
        describe(binding.settingsKeepAwake, repository.keepAwake)
        describe(binding.settingsReducedMotion, repository.reducedMotion)

        binding.settingsAutoAdvance.setOnCheckedChangeListener { button, checked ->
            repository.autoAdvance = checked
            describe(button, checked)
        }
        binding.settingsKeepAwake.setOnCheckedChangeListener { button, checked ->
            repository.keepAwake = checked
            describe(button, checked)
            applyKeepAwake(checked)
        }
        binding.settingsReducedMotion.setOnCheckedChangeListener { button, checked ->
            repository.reducedMotion = checked
            describe(button, checked)
        }

        checkLength(repository.defaultStepMinutes)
        binding.settingsLengths.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            repository.defaultStepMinutes = when (checkedId) {
                R.id.length_one -> 1
                R.id.length_five -> 5
                R.id.length_ten -> 10
                else -> 2
            }
        }

        binding.settingsReset.setOnClickListener { confirmReset() }
        applyKeepAwake(repository.keepAwake)
    }

    private fun describe(view: View, checked: Boolean) {
        ViewCompat.setStateDescription(
            view,
            getString(if (checked) R.string.state_on else R.string.state_off)
        )
    }

    private fun checkLength(minutes: Int) {
        val binding = _binding ?: return
        val id = when (minutes) {
            1 -> R.id.length_one
            5 -> R.id.length_five
            10 -> R.id.length_ten
            else -> R.id.length_two
        }
        binding.settingsLengths.check(id)
    }

    private fun applyKeepAwake(enabled: Boolean) {
        if (!isAdded) return
        val window = activity?.window ?: return
        if (enabled) {
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun confirmReset() {
        if (!isAdded) return
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_Alert)
            .setTitle(R.string.settings_reset_title)
            .setMessage(R.string.settings_reset_body)
            .setPositiveButton(R.string.settings_reset_confirm) { dialog, _ ->
                ServiceLocator.provide(requireContext()).runHistory.clear()
                dialog.dismiss()
                dismissAllowingStateLoss()
            }
            .setNegativeButton(R.string.cta_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        settings = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "settings_dialog"
    }
}
