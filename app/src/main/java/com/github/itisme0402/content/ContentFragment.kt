package com.github.itisme0402.content

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.itisme0402.R
import kotlinx.android.synthetic.main.fragment_content.*

open class ContentFragment : Fragment(R.layout.fragment_content) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dividerItemDecoration =
            DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        contentRecyclerView.addItemDecoration(dividerItemDecoration)
    }
}
