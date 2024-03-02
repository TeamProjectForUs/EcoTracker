package com.example.greenapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post
import com.example.greenapp.Modules.Posts.Adapter.PostsRecyclerAdapter
import com.example.greenapp.Modules.Posts.PostsRecyclerViewActivity
import com.example.greenapp.databinding.FragmentPostsBinding


class MyPostsFragment : Fragment() {

        var postsRcyclerView: RecyclerView? = null
        var posts: List<Post>? = null
        var adapter: PostsRecyclerAdapter? = null
        var progressBar: ProgressBar? = null

        private var _binding: FragmentPostsBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {


            _binding = FragmentPostsBinding.inflate(inflater, container, false)
            val view = binding.root

            progressBar = binding.progressBar

            progressBar?.visibility = View.VISIBLE


            Model.instance.getMyPosts { posts ->
                this.posts = posts
                adapter?.posts = posts
                adapter?.notifyDataSetChanged()

                progressBar?.visibility = View.GONE
            }

            postsRcyclerView = binding.rvPostsFragmentList
            postsRcyclerView?.setHasFixedSize(true)
            postsRcyclerView?.layoutManager = LinearLayoutManager(context)
            adapter = PostsRecyclerAdapter(posts)
            adapter?.listener = object : PostsRecyclerViewActivity.OnItemClickListener {

                override fun onItemClick(position: Int) {
                    Log.i("TAG", "PostsRecyclerAdapter: Position clicked $position")
                    val post = posts?.get(position)
                    post?.let {
                        val action= MyPostsFragmentDirections.actionMyPostsFragmentToPostFullViewFragment(it.postUid,it.uri,it.name,it.description,it.id)
                        Navigation.findNavController(view).navigate(action)
                    }
                }

                override fun onStudentClicked(post: Post?) {
                    Log.i("TAG", "POST $post")
                }
            }

            postsRcyclerView?.adapter = adapter

            val addPostButton: ImageButton = view.findViewById(R.id.ibtnPostsFragmentAddPost)

            val action = Navigation.createNavigateOnClickListener(R.id.action_myPostsFragment_to_addPostFragment)
            addPostButton.setOnClickListener(action)

            return view
        }

        override fun onResume() {
            super.onResume()

            progressBar?.visibility = View.VISIBLE

            Model.instance.getMyPosts { posts ->
                this.posts = posts
                adapter?.posts = posts
                adapter?.notifyDataSetChanged()

                progressBar?.visibility = View.GONE
            }
        }

        override fun onDestroy() {
            super.onDestroy()

            _binding = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }


}