package com.example.greenapp.adapters;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.FeedFragment
import com.example.greenapp.models.Post
import com.example.greenapp.R
import com.squareup.picasso.Picasso

class PostsRecyclerAdapter(
    val currentUserId: String,
    var posts: List<Post>,
    var onProfile: Boolean = false
) : RecyclerView.Adapter<PostsRecyclerAdapter.PostViewHolder>() {
    var listener: FeedFragment.OnItemClickListener? = null

    override fun getItemCount(): Int = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.post_layout_row, parent, false)
        return PostViewHolder(itemView, listener, posts,onProfile)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts.get(position)
        holder.bind(post, currentUserId)
    }
    class PostViewHolder(
        private val itemView: View,
        private val listener: FeedFragment.OnItemClickListener?,
        var posts: List<Post>?,
        var onProfile: Boolean = false,
    ) : RecyclerView.ViewHolder(itemView) {

        private var imageView: ImageView? = null
        private var desTextView: TextView? = null
        private var postEditBtn: ImageView? = null
        private var postDeleteBtn: ImageView? = null

        private var avatar: ImageView? = null
        var post: Post? = null

        init {
            imageView = itemView.findViewById(R.id.tvPostListRowImage)
            postEditBtn = itemView.findViewById(R.id.ivEdit)
            desTextView = itemView.findViewById(R.id.tvPostListRowDes)
            postDeleteBtn = itemView.findViewById(R.id.ivDelete)
            avatar = itemView.findViewById(R.id.ivPostListRowAvatar)

            itemView.setOnClickListener {

            }
        }

        fun bind(post: Post, currentUserId: String) {
            this.post = post
            Picasso.get().load(post.uri.toUri()).resize(1000, 1000).centerInside().into(avatar)

            desTextView?.text = post.description
            if (!onProfile) {
                postDeleteBtn?.visibility = View.GONE
            }
            if(post.userId != currentUserId) {
                postDeleteBtn?.visibility = View.GONE
                postEditBtn?.visibility = View.GONE
            }
            if (!post.isDefaultImage()) {
                Picasso.get()
                    .load(post.uri.toUri())
                    .resize(1000, 1000)
                    .centerInside()
                    .into(imageView)
            } else {
                imageView?.visibility = View.GONE
            }
            Picasso.get()
                .load(post.userUri.toUri())
                .resize(1000, 1000)
                .centerInside()
                .into(avatar)
            postEditBtn?.setOnClickListener {
                Log.i("TAG", "PostViewHolder: Position clicked $adapterPosition")
                listener?.onItemClick(adapterPosition)
            }
            if(currentUserId != post.userId) {
                itemView.setOnClickListener {
                    listener?.onItemClick(adapterPosition)
                }
            }
            if (onProfile) {
                postDeleteBtn?.setOnClickListener {
                    listener?.onItemClickRemove(position)
                }
            }
        }
    }

}
