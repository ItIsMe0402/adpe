package com.github.itisme0402.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.itisme0402.R
import com.github.itisme0402.entity.Post
import kotlinx.android.synthetic.main.fragment_content.*

class PostListFragment : ContentFragment<Post>() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.loadPosts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postsStateLiveData.bindToLayout()
    }

    override fun updateListContent(content: List<Post>) {
        contentRecyclerView.adapter = Adapter(content)
    }

    private inner class Adapter(private val posts: List<Post>) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
                as TextView
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = posts[position].body
        }

        override fun getItemCount() = posts.size
    }

    private class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}
