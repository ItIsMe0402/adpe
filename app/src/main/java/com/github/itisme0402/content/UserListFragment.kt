package com.github.itisme0402.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.itisme0402.InjectAllStuffViewModelFactory
import com.github.itisme0402.MainViewModel
import com.github.itisme0402.R
import com.github.itisme0402.State
import com.github.itisme0402.databinding.ItemUserBinding
import com.github.itisme0402.entity.User
import kotlinx.android.synthetic.main.fragment_content.*

class UserListFragment : ContentFragment() {

    private val viewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(activity!!, InjectAllStuffViewModelFactory)
            .get(MainViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.loadSomeUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.usersStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is State.Loaded -> {
                    contentLoadingIndicator.visibility = View.GONE
                    contentRecyclerView.visibility = View.VISIBLE
                    contentRecyclerView.adapter = Adapter(state.content)
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

    class Adapter(private val users: List<User>) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DataBindingUtil.inflate<ItemUserBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_user,
                parent,
                false
            )
            return ViewHolder(binding, binding.root)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateUser(users[position])
        }

        override fun getItemCount() = users.size
    }

    class ViewHolder(
        private val binding: ItemUserBinding,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun updateUser(user: User) {
            binding.user = user
        }
    }
}
