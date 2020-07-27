package com.github.itisme0402.content

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.itisme0402.InjectAllStuffViewModelFactory
import com.github.itisme0402.MainViewModel
import com.github.itisme0402.R
import com.github.itisme0402.State
import kotlinx.android.synthetic.main.fragment_content.*

abstract class ContentFragment<T> : Fragment(R.layout.fragment_content) {

    protected open val shouldShowFooter = false

    protected val viewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(activity!!, InjectAllStuffViewModelFactory)
            .get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration =
            DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        contentRecyclerView.addItemDecoration(dividerItemDecoration)
    }

    protected fun LiveData<State<T>>.bindToLayout() {
        observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loaded -> {
                    if (shouldShowFooter) {
                        footer.visibility = View.VISIBLE
                    }
                    contentLoadingIndicator.visibility = View.GONE
                    contentRecyclerView.visibility = View.VISIBLE
                    updateContent(state.content)
                }
                State.Loading -> {
                    footer.visibility = View.GONE
                    contentLoadingIndicator.visibility = View.VISIBLE
                    contentRecyclerView.visibility = View.GONE
                }
                is State.Error -> {
                    footer.visibility = View.GONE
                    contentLoadingIndicator.visibility = View.GONE
                    contentRecyclerView.visibility = View.GONE
                    AlertDialog.Builder(activity!!)
                        .setMessage(getString(R.string.format_error_message, state.message))
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .show()
                }
            }
        })
    }

    protected abstract fun updateContent(content: T)
}
