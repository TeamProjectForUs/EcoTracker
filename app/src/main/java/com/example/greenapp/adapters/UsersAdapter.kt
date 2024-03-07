package com.example.greenapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.models.User
import com.example.greenapp.databinding.UserSearchRowBinding
import com.squareup.picasso.Picasso

class UsersAdapter(
    private var users: List<User>,
    private val onUserClick: (User) -> Unit,
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {


    fun setUsers(users: List<User>?) {
        users?.let {
            this.users = it
            notifyDataSetChanged()
        }
    }

    class UserViewHolder(val binding: UserSearchRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            Picasso.get()
                .load(user.getImage())
                .resize(5000, 5000)
                .centerInside()
                .into(binding.userRowImageView)

            binding.userRowNameTv.text = user.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserSearchRowBinding.inflate(LayoutInflater.from(parent.context))
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }
}