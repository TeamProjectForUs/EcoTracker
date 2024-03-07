package com.example.greenapp.modules.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.R
import com.example.greenapp.adapters.PhotosAdapter

class MyPhotosFragment : Fragment() {


    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var photosRv: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(requireParentFragment())[ProfileViewModel::class.java]

        photosRv = view.findViewById(R.id.photosRv)
        photosRv.layoutManager = GridLayoutManager(context, 3)

        profileViewModel.myPhotos.observe(viewLifecycleOwner) {
            val adapter = PhotosAdapter(it)
            photosRv.adapter = adapter
        }


    }
}