package com.example.greenapp.Modules.Posts.Adapter;


import android.content.Context
import android.net.Uri
import com.example.greenapp.Model.Post
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Model
import com.example.greenapp.Modules.Posts.PostsRecyclerViewActivity
import com.example.greenapp.R
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.io.path.fileVisitor

class PostViewHolder (
    private val itemView:View,
    private val listener: PostsRecyclerViewActivity.OnItemClickListener?,
    var posts:List<Post>?): RecyclerView.ViewHolder(itemView) {

    private var nameTextView: TextView? = null
    private var desTextView:TextView?=null
    private var postCheckbox: CheckBox? = null
    private var avatar:ImageView?=null
    var post: Post? = null

    init {
        nameTextView = itemView.findViewById(R.id.tvPostListRowName)
        postCheckbox = itemView.findViewById(R.id.cbPostListRow)
        desTextView  = itemView.findViewById(R.id.tvPostListRowDes)
        avatar=itemView.findViewById(R.id.ivPostListRowAvatar)

        postCheckbox?.setOnClickListener {
            val post = posts?.get(adapterPosition)
            post?.isChecked = postCheckbox?.isChecked ?: false
        }

        itemView.setOnClickListener {
            Log.i("TAG", "PostViewHolder: Position clicked $adapterPosition")

            listener?.onItemClick(adapterPosition)
            listener?.onStudentClicked(post)
        }
    }

    fun bind(post: Post?) {
        this.post = post
        nameTextView?.text = post?.name
        desTextView?.text=post?.description

        Picasso.get().load(post?.uri?.toUri()).resize(1000, 1000).centerInside().into(avatar)
        // idTextView?.text = post?.id
        postCheckbox?.apply {
            isChecked = post?.isChecked ?: false
        }
    }
}
