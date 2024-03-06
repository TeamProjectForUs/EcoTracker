package com.example.greenapp.Modules.Posts.Adapter;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.FeedFragment
import com.example.greenapp.Model.Post
import com.example.greenapp.R

class PostsRecyclerAdapter(
    var posts: List<Post>,
    var onProfile: Boolean = false
) : RecyclerView.Adapter<PostViewHolder>() {
    var listener: FeedFragment.OnItemClickListener? = null

    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.post_layout_row, parent, false)
        return PostViewHolder(itemView, listener, posts,onProfile)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts.get(position)
        holder.bind(post)
    }
}
