package com.example.greenapp.Modules.Connection

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.greenapp.Model.Model
import com.example.greenapp.R
import com.example.greenapp.Model.Post
import com.squareup.picasso.Picasso


class AddPostFragment : Fragment() {

    private var nameTextField: EditText? = null
    private var photoButton: Button? = null
    private var descriptionTextField: EditText? = null
    private var imageField: ImageView? = null
   // private var messageTextView: TextView? = null
    private var saveButton: Button? = null
    private var cancelButton: Button? = null
    private var uri:Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle("Add post")

        nameTextField = view.findViewById(R.id.etAddPostName)
        saveButton = view.findViewById(R.id.btnAddPostSave)
        cancelButton = view.findViewById(R.id.btnAddPostCancel)
        imageField=view.findViewById(R.id.image)
        photoButton=view.findViewById(R.id.btnAddPostSavePhoto)
        descriptionTextField=view.findViewById(R.id.description)

        val pickImage= registerForActivityResult(ActivityResultContracts.GetContent()) {
           // binding.imageField.setImageURI(it)
            Picasso.get().load(it).resize(1000, 1000).centerInside().into(imageField)
            if (it != null) {
                uri = it
            }
        }
        photoButton?.setOnClickListener{
            pickImage.launch("image/*")
        }


        cancelButton?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_addPostFragment_to_feedFragment)
        }

        saveButton?.setOnClickListener {
            val name = nameTextField?.text.toString()
            val des=descriptionTextField?.text.toString()

            Model.instance.getCurrentUser {
                val url=uri.toString()
                val post = Post(name,it[2],url,des,false,"")
                Model.instance.addPost(post) {
                    Navigation.findNavController(view)
                        .navigate(R.id.action_addPostFragment_to_feedFragment)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}