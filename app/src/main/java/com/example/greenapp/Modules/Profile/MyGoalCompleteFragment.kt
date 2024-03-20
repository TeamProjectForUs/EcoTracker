package com.example.greenapp.modules.Profile

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.greenapp.databinding.FragmentCompleteGoalBinding
import com.example.greenapp.models.Goal

class MyGoalCompleteFragment(
    val goal: Goal,
    val callbackPublish: () -> Unit,
) : DialogFragment() {

    private var _binding: FragmentCompleteGoalBinding? = null
    private val binding: FragmentCompleteGoalBinding get() = _binding!!
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCompleteGoalBinding.inflate(layoutInflater)
        binding.closeCompleteBtn.setOnClickListener {
            dismiss()
        }
        binding.keepAchieveBtn.setOnClickListener {
            dismiss()
        }
        binding.publishAchieveBtn.setOnClickListener {
            callbackPublish()
            dismiss()
            Toast.makeText(requireContext(), "Achievement post published!", Toast.LENGTH_SHORT)
                .show()
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        binding.goalTvComplete.text = goal.tip.description
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

}