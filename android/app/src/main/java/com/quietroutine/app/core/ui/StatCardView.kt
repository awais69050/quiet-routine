package com.quietroutine.app.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.quietroutine.app.R
import com.quietroutine.app.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    private var accentColor: Int = ContextCompat.getColor(context, R.color.sage)

    init {
        if (attrs != null) {
            val typed = context.obtainStyledAttributes(attrs, R.styleable.StatCardView)
            accentColor = typed.getColor(R.styleable.StatCardView_statAccent, accentColor)
            val value = typed.getString(R.styleable.StatCardView_statValue).orEmpty()
            val label = typed.getString(R.styleable.StatCardView_statLabel).orEmpty()
            binding.statValue.text = value
            binding.statLabel.text = label
            typed.recycle()
        }
        applyAccent()
    }

    fun setAccent(color: Int) {
        accentColor = color
        applyAccent()
    }

    fun bind(value: String, label: String) {
        binding.statValue.text = value
        binding.statLabel.text = label
        visibility = View.VISIBLE
    }

    fun bindOrHide(value: Int, label: String) {
        if (value <= 0) {
            visibility = View.GONE
            return
        }
        bind(value.toString(), label)
    }

    fun hide() {
        visibility = View.GONE
    }

    fun isCardVisible(): Boolean = visibility == View.VISIBLE

    private fun applyAccent() {
        val tint = ColorStateList.valueOf(accentColor)
        binding.statAccent.backgroundTintList = tint
        binding.statValue.setTextColor(accentColor)
    }
}
