package com.example.ecotracker


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class PostAdapter(private var postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {


    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.titleTextView.text = post.title
        holder.contentTextView.text = post.content
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun setData(updatedData: List<Post>) {
        postList = updatedData
        notifyDataSetChanged()
    }


}
