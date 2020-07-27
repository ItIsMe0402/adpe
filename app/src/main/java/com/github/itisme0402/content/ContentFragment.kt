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

open class ContentFragment<T> : Fragment(R.layout.fragment_content) {

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

    protected fun LiveData<State<List<T>>>.bindToLayout() {
        observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loaded -> {
                    contentLoadingIndicator.visibility = View.GONE
                    contentRecyclerView.visibility = View.VISIBLE
                    updateListContent(state.content)
                }
                State.Loading -> {
                    contentLoadingIndicator.visibility = View.VISIBLE
                    contentRecyclerView.visibility = View.GONE
                    contentRecyclerView.adapter = null
                }
                is State.Error -> {
                    contentLoadingIndicator.visibility = View.GONE
                    contentRecyclerView.visibility = View.GONE
                    contentRecyclerView.adapter = null
                    AlertDialog.Builder(activity!!)
                        .setMessage(getString(R.string.format_error_message, state.message))
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .show()
                }
            }
        })
    }

    protected open fun updateListContent(content: List<T>) {
    }
}
