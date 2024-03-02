package com.example.greenapp.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Post
import com.example.greenapp.Modules.Posts.Adapter.PostsRecyclerAdapter
import com.example.greenapp.Modules.Posts.PostsRecyclerViewActivity
import com.example.greenapp.Modules.ProfileViewModel
import com.example.greenapp.databinding.FragmentMyPostsBinding


class MyPostsFragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var posts: List<Post>
    private lateinit var adapter: PostsRecyclerAdapter
    private lateinit var progressBar: ProgressBar

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(requireParentFragment())[ProfileViewModel::class.java]
        progressBar = binding.progressBar
        postsRecyclerView = binding.rvPostsFragmentList
        progressBar.visibility = View.VISIBLE

        profileViewModel.myPosts.observe(viewLifecycleOwner) {
            this.posts = it
            adapter.posts = it
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }

        postsRecyclerView.setHasFixedSize(true)
        postsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(listOf())

        adapter.listener = object : PostsRecyclerViewActivity.OnItemClickListener {

            override fun onItemClick(position: Int) {
                Log.i("TAG", "PostsRecyclerAdapter: Position clicked $position")
                val post = posts[position]
                post.let {
                    val action =
                        MyPostsFragmentDirections.actionMyPostsFragmentToPostFullViewFragment(
                            it.postUid,
                            it.uri,
                            it.name,
                            it.description,
                            it.id
                        )
                    Navigation.findNavController(view).navigate(action)
                }
            }

            override fun onStudentClicked(post: Post?) {
                Log.i("TAG", "POST $post")
            }
        }

        postsRecyclerView.adapter = adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}