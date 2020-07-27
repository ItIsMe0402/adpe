package com.github.itisme0402.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.itisme0402.R
import com.github.itisme0402.databinding.ItemUserBinding
import com.github.itisme0402.entity.User
import kotlinx.android.synthetic.main.fragment_content.*

class UserListFragment : ContentFragment<User>() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.loadSomeUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.usersStateLiveData.bindToLayout()
    }

    override fun updateListContent(content: List<User>) {
        contentRecyclerView.adapter = Adapter(content)
    }

    private inner class Adapter(private val users: List<User>) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DataBindingUtil.inflate<ItemUserBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_user,
                parent,
                false
            )
            return ViewHolder(binding, binding.root)
                .apply {
                    itemView.setOnClickListener {
                        viewModel.onUserChosen(users[adapterPosition].id)
                    }
                }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateUser(users[position])
        }

        override fun getItemCount() = users.size
    }

    private class ViewHolder(
        private val binding: ItemUserBinding,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun updateUser(user: User) {
            binding.user = user
        }
    }
}
