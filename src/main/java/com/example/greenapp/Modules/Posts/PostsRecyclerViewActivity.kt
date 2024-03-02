package com.example.greenapp.Modules.Posts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post
import com.example.greenapp.Modules.Posts.Adapter.PostsRecyclerAdapter
import com.example.greenapp.databinding.ActivityPostsRcyclerViewBinding

class PostsRecyclerViewActivity : AppCompatActivity() {
    var postsRcyclerView: RecyclerView? = null
    var posts: List<Post>? = null
    var adapter: PostsRecyclerAdapter? = null

    private lateinit var binding: ActivityPostsRcyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostsRcyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)



        postsRcyclerView = binding.rvPostREcyclerList
        postsRcyclerView?.setHasFixedSize(true)
        postsRcyclerView?.layoutManager = LinearLayoutManager(this)

        adapter = PostsRecyclerAdapter(posts)
        adapter?.listener = object : OnItemClickListener {

            override fun onItemClick(position: Int) {
                Log.i("TAG", "StudentsRecyclerAdapter: Position clicked $position")
            }

            override fun onStudentClicked(post: Post?) {
                Log.i("TAG", "POST $post")
            }
        }

        postsRcyclerView?.adapter = adapter
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) // Student
        fun onStudentClicked(post: Post?)
    }

    override fun onResume() {
        super.onResume()

//        Model.instance.getAllPosts { posts ->
//            this.posts = posts
//            adapter?.posts = posts
//            adapter?.notifyDataSetChanged()
//        }
    }
}