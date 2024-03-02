package com.example.greenapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.greenapp.Modules.ProfileViewModel
import com.example.greenapp.databinding.FragmentMyTipsBinding


class MyTipsFragment : Fragment() {

    private var _binding: FragmentMyTipsBinding? = null
    private val binding: FragmentMyTipsBinding get() = _binding!!


    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMyTipsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(requireParentFragment())[ProfileViewModel::class.java]
        profileViewModel.myPhotos
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}