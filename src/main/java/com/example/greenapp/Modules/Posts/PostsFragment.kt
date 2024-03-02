package com.example.greenapp.Modules.Posts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.Post
import com.example.greenapp.Modules.Posts.Adapter.PostsRecyclerAdapter
import com.example.greenapp.R
import com.example.greenapp.databinding.FragmentPostsBinding


class PostsFragment : Fragment() {

    var postsRcyclerView: RecyclerView? = null
    var adapter: PostsRecyclerAdapter? = null
    var progressBar: ProgressBar? = null

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[PostsViewModel::class.java]
        progressBar = binding.progressBar

        progressBar?.visibility = View.VISIBLE

        viewModel.posts=Model.instance.getAllPosts()

        postsRcyclerView = binding.rvPostsFragmentList
        postsRcyclerView?.setHasFixedSize(true)
        postsRcyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(viewModel.posts?.value)

        adapter?.listener = object : PostsRecyclerViewActivity.OnItemClickListener {

            override fun onItemClick(position: Int) {
                Log.i("TAG", "StudentsRecyclerAdapter: Position clicked $position")
                val post = viewModel.posts?.value?.get(position)
                post?.let {
//                    val action=PostsFragmentDirections.actionGlobalPostFullViewFragment()
//                    Navigation.findNavController(view).navigate(action)
                }
            }

            override fun onStudentClicked(post: Post?) {
                Log.i("TAG", "POST $post")
            }
        }

        postsRcyclerView?.adapter = adapter

        val addPostButton: ImageButton = view.findViewById(R.id.ibtnPostsFragmentAddPost)

        val action = Navigation.createNavigateOnClickListener(R.id.action_feedFragment_to_addPostFragment)
        addPostButton.setOnClickListener(action)

        viewModel.posts?.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }

        binding.pullToRefresh.setOnRefreshListener {
            reloadData()
        }

        Model.instance.postsListLoadingState.observe(viewLifecycleOwner) { state ->
            binding.pullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }
    private fun reloadData() {
        progressBar?.visibility = View.VISIBLE
        Model.instance.refreshAllPosts()
        progressBar?.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}