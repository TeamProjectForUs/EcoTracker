package com.example.greenapp

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.adapters.PostsRecyclerAdapter
import com.example.greenapp.database.Model
import com.example.greenapp.databinding.FragmentFeedBinding
import com.example.greenapp.modules.Common.SharedViewModel
import com.example.greenapp.modules.Profile.createNotificationsMenu
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class FeedFragment : BaseMenuFragment() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemClickRemove(position: Int)
    }

    private var postsRecyclerView: RecyclerView? = null
    private var adapter: PostsRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = getSharedViewModel()
        progressBar = binding.progressBar

        progressBar?.visibility = View.VISIBLE

        postsRecyclerView = binding.rvPostsFragmentList
        postsRecyclerView?.setHasFixedSize(true)
        postsRecyclerView?.layoutManager = LinearLayoutManager(context)
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                adapter = PostsRecyclerAdapter(
                    currentUserId = user.id,
                    viewModel.posts.value ?: listOf()
                )
                adapter?.listener = object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val post = viewModel.posts.value?.get(position)
                        post?.let {
                            val action =
                                FeedFragmentDirections.actionFeedFragmentToPostFullViewFragment(
                                    it.postUid,
                                    it.uri,
                                    it.name,
                                    it.description,
                                    it.userId
                                )
                            Navigation.findNavController(view).navigate(action)
                        }
                    }
                    override fun onItemClickRemove(position: Int) {

                    }
                }
                postsRecyclerView?.adapter = adapter
            }
        }




        createNotificationsMenu(binding.notificationsFeedBtn)


        val addPostButton: ImageView = view.findViewById(R.id.add_post_feedBtn)

        val action =
            Navigation.createNavigateOnClickListener(R.id.action_feedFragment_to_addPostFragment)
        addPostButton.setOnClickListener(action)

        viewModel.posts.observe(viewLifecycleOwner) {
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }

        binding.pullToRefresh.setOnRefreshListener {
            reloadData()
        }

        viewModel.postsListLoadingState.observe(viewLifecycleOwner) { state ->
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
        viewModel.refreshPosts()
        progressBar?.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}