package com.example.greenapp.Modules.Connection

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.greenapp.BaseFragment
import com.example.greenapp.MainActivity
import com.example.greenapp.R
import com.example.greenapp.Model.Post
import com.example.greenapp.SharedViewModel
import com.squareup.picasso.Picasso


class AddPostFragment : BaseFragment() {

    private var photoButton: ImageView? = null
    private var descriptionTextField: EditText? = null

    private var saveButton: Button? = null
    private var cancelButton: Button? = null
    private var uri: Uri? = null

    private lateinit var viewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        setupUI(view)
        viewModel = getSharedViewModel()
        return view
    }

    private fun setupUI(view: View) {

        saveButton = view.findViewById(R.id.btnAddPostSave)
        photoButton = view.findViewById(R.id.btnAddPostSavePhoto)
        descriptionTextField = view.findViewById(R.id.description)

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                uri = it
            }
        }
        photoButton?.setOnClickListener {
            pickImage.launch("image/*")
        }


        cancelButton?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_addPostFragment_to_feedFragment)
        }

        saveButton?.setOnClickListener {
            val name = "Post"
            val des = descriptionTextField?.text.toString()

            viewModel
                .currentUser.value?.let {
                    val url = uri.toString()
                    val userId = it.id
                    val post = if (uri != null) {
                        if (it.uri.trim().isEmpty()) {
                            Post(
                                userId = userId,
                                name = name,
                                uri = url,
                                description = des,
                                isChecked = false,
                                postUid = "",
                            )
                        } else {
                            Post(
                                userId = userId,
                                name = name,
                                uri = url,
                                userUri = it.uri,
                                description = des,
                                isChecked = false,
                                postUid = "",
                            )
                        }
                    } else {
                        if (it.uri.trim().isEmpty()) {
                            Post(
                                userId = userId,
                                name = name,
                                description = des,
                                isChecked = false,
                                postUid = ""
                            )
                        } else {
                            Post(
                                userId = userId,
                                name = name,
                                userUri = it.uri,
                                description = des,
                                isChecked = false,
                                postUid = ""
                            )
                        }
                    }
                    viewModel.addPost(post) {
                        findNavController().popBackStack()
                        (activity as MainActivity).setupNormalMenu()
                        Toast.makeText(requireContext(), "Posted successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                }

        }
    }

}