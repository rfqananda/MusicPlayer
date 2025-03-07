package com.example.uicomponent.search_bar

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.uicomponent.R
import com.example.uicomponent.databinding.SearchBarViewBinding
import com.example.uicomponent.extension.invisibleIf

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: SearchBarViewBinding = SearchBarViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var onSearchActionListener: ((String) -> Unit)? = null

    var onEndIconClickListener: (() -> Unit)? = null

    init {
        setupAttributes(attrs)
        setupClearTextListener()
        setupSearchActionListener()
        setupEndIconVisibilityLogic()
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.SearchBarView,
            0,
            0
        ).apply {
            try {
                val startIcon = getResourceId(
                    R.styleable.SearchBarView_startIcon,
                    R.drawable.search
                )
                val endIcon = getResourceId(
                    R.styleable.SearchBarView_endIcon,
                    R.drawable.close
                )
                val hint = getString(R.styleable.SearchBarView_hint) ?: "Cari..."

                binding.startIcon.setImageResource(startIcon)
                binding.endIcon.setImageResource(endIcon)
                binding.etSearch.hint = hint
                updateEndIconVisibility()
            } finally {
                recycle()
            }
        }
    }

    private fun setupEndIconVisibilityLogic() = with(binding) {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateEndIconVisibility()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etSearch.setOnFocusChangeListener { _, hasFocus ->
            val background = if (hasFocus) {
                R.drawable.bg_search_bar_view_focused
            } else {
                R.drawable.bg_search_bar_view
            }
            container.setBackgroundResource(background)
        }
    }

    private fun updateEndIconVisibility() = with(binding) {
        val hasText = !etSearch.text.isNullOrEmpty()

        endIcon.invisibleIf(!hasText)
        endIcon.isClickable = hasText
    }


    private fun setupClearTextListener() = with(binding) {
        endIcon.setOnClickListener {
            etSearch.text?.clear()
            onEndIconClickListener?.invoke()
            clearFocus()
            hideKeyboard()
        }
    }

    private fun setupSearchActionListener() = with(binding) {
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = etSearch.text.toString()

                onSearchActionListener?.invoke(searchText)

                hideKeyboard()

                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun hideKeyboard() = with(binding) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        clearFocus()
        updateEndIconVisibility()
    }
}
