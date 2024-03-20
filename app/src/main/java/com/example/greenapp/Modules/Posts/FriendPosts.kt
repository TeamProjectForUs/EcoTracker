package com.example.greenapp.modules.Posts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenapp.adapters.PostsRecyclerAdapter
import com.example.greenapp.databinding.FragmentFriendPostsBinding

class FriendPosts(val adapter: PostsRecyclerAdapter) : DialogFragment() {

    private var _binding: FragmentFriendPostsBinding? = null
    private val binding: FragmentFriendPostsBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentFriendPostsBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        binding.rvFriendPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriendPosts.adapter = adapter
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
        return dialog
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }
}