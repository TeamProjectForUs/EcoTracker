package com.example.greenapp.modules.Posts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenapp.BaseMenuFragment
import com.example.greenapp.adapters.FriendGoalsAdapter
import com.example.greenapp.databinding.FragmentFriendGoalsBinding
import com.example.greenapp.models.Goal

class FriendAchievements(val achievements: List<Goal>): DialogFragment() {
    private var _binding: FragmentFriendGoalsBinding? = null
    private val binding: FragmentFriendGoalsBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentFriendGoalsBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        val adapter = FriendGoalsAdapter(achievements)
        binding.rvFriendGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriendGoals.adapter = adapter
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