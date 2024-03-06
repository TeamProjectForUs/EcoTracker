package com.example.greenapp.Modules.Profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.BaseFragment
import com.example.greenapp.FeedFragment
import com.example.greenapp.Model.Post
import com.example.greenapp.Modules.Posts.Adapter.PostsRecyclerAdapter
import com.example.greenapp.R
import com.example.greenapp.databinding.FragmentMyPostsBinding
import com.google.android.material.button.MaterialButton


class MyPostsFragment : BaseFragment() {


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

        val sharedVm = getSharedViewModel()
        postsRecyclerView.setHasFixedSize(true)
        postsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PostsRecyclerAdapter(listOf(), onProfile = true)
        val userId = sharedVm.currentUser.value?.id ?: ""
        adapter.listener = object : FeedFragment.OnItemClickListener {

            override fun onItemClick(position: Int) {
                val post = posts[position]
                post.let {
                    val action =
                        MyPostsFragmentDirections.actionMyPostsFragmentToPostFullViewFragment(
                            it.postUid,
                            it.uri,
                            it.name,
                            it.description,
                            userId,
                        )
                    Navigation.findNavController(view).navigate(action)
                }
            }

            override fun onItemClickRemove(position: Int) {
                val post = posts[position]
                val layout = layoutInflater.inflate(R.layout.confirmation_layout, null, false)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(layout)
                    .create()
                val okBtn = layout.findViewById<MaterialButton>(R.id.deleteBtnYes)
                val cancelBtn = layout.findViewById<MaterialButton>(R.id.deleteBtnNo)

                okBtn.setOnClickListener {
                    profileViewModel.removeMyPost(post)
                    dialog.dismiss()
                }
                cancelBtn.setOnClickListener { dialog.dismiss() }
                dialog.show()

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