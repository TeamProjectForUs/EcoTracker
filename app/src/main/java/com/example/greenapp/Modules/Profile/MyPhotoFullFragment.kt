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
import com.example.greenapp.databinding.FragmentPhotoFullBinding
import com.example.greenapp.models.Goal
import com.squareup.picasso.Picasso

class MyPhotoFullFragment(
    private val photo: String,
) : DialogFragment() {

    private var _binding: FragmentPhotoFullBinding? = null
    private val binding: FragmentPhotoFullBinding get() = _binding!!
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentPhotoFullBinding.inflate(layoutInflater)
        binding.closeCompleteBtn.setOnClickListener {
            dismiss()
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        Picasso.get().load(photo).into(binding.imgFull)
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