package com.example.greenapp.Modules.Posts.Adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Post
import com.example.greenapp.Modules.Posts.PostsRecyclerViewActivity
import com.example.greenapp.R

class PostsRecyclerAdapter(var posts: List<Post>?): RecyclerView.Adapter<PostViewHolder>() {
    var listener: PostsRecyclerViewActivity.OnItemClickListener? = null

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_layout_row, parent, false)
        return PostViewHolder(itemView, listener, posts)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val student = posts?.get(position)
        holder.bind(student)
    }
}
