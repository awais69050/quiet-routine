package com.quietroutine.app.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.quietroutine.app.R
import com.quietroutine.app.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        if (attrs != null) {
            val typed = context.obtainStyledAttributes(attrs, R.styleable.SectionHeaderView)
            binding.sectionTitle.text =
                typed.getString(R.styleable.SectionHeaderView_sectionTitle).orEmpty()
            val action = typed.getString(R.styleable.SectionHeaderView_sectionAction)
            val actionVisible =
                typed.getBoolean(R.styleable.SectionHeaderView_sectionActionVisible, false)
            if (action != null && actionVisible) {
                binding.sectionAction.text = action
                binding.sectionAction.visibility = View.VISIBLE
            }
            typed.recycle()
        }
    }

    fun setTitle(title: String) {
        binding.sectionTitle.text = title
    }

    fun setAction(label: String, action: () -> Unit) {
        binding.sectionAction.text = label
        binding.sectionAction.visibility = View.VISIBLE
        binding.sectionAction.setOnClickListener { action() }
    }

    fun clearAction() {
        binding.sectionAction.setOnClickListener(null)
        binding.sectionAction.visibility = View.GONE
    }
}
