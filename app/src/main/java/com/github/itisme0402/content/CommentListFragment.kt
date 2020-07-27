package com.github.itisme0402.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.itisme0402.MainViewModel
import com.github.itisme0402.databinding.ItemCommentBinding
import com.github.itisme0402.entity.Comment
import kotlinx.android.synthetic.main.fragment_content.*

class CommentListFragment : ContentFragment<MainViewModel.CommentPage>() {

    override val shouldShowFooter = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.loadComments()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pagedCommentsStateLiveData.bindToLayout()
        prevPageButton.setOnClickListener { viewModel.prevPageOfComments() }
        nextPageButton.setOnClickListener { viewModel.nextPageOfComments() }
    }

    override fun updateContent(content: MainViewModel.CommentPage) {
        if (contentRecyclerView.adapter !is Adapter) {
            contentRecyclerView.adapter = Adapter()
        }
        (contentRecyclerView.adapter as Adapter).submitList(content.pageContent)
        pageInfoTextView.text = content.footerText
    }

    private inner class Adapter : ListAdapter<Comment, ViewHolder>(object : DiffUtil.ItemCallback<Comment>() {

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setComment(getItem(position))
        }
    }

    private class ViewHolder(
        private val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setComment(comment: Comment) {
            binding.comment = comment
        }
    }
}
