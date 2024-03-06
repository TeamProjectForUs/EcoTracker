package com.example.greenapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.example.greenapp.Model.Model
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class EditProfileFragment : BaseMenuFragment() {
    private var imageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var nameEditTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var name: String? = null
    private var uri: String? = null
    private var saveButton: Button? = null
    private var photoButton: MaterialButton? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_edit_my_profile, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {

        imageView = view.findViewById(R.id.userPic)
        nameEditTextView = view.findViewById(R.id.usernameId)
        saveButton = view.findViewById(R.id.editProfileSaveBtn)
        photoButton = view.findViewById(R.id.ChangeProfilePictureBtn)

        getSharedViewModel()
            .currentUser
            .observe(viewLifecycleOwner) { user ->
                Toast.makeText(context, " we got here.", Toast.LENGTH_SHORT).show()
                name = user.name
                uri = user.uri
                nameEditTextView?.text = name
                Picasso.get()
                    .load(user.getImage().toUri())
                    .resize(1000, 1000)
                    .centerInside()
                    .into(imageView)
                getSharedViewModel()
                    .currentUser
                    .removeObservers(viewLifecycleOwner)
            }


        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            Picasso.get().load(it).resize(5000, 5000).centerInside().into(imageView)

            if (it != null) {
                uri = it.toString()
            }
        }
        photoButton?.setOnClickListener {
            pickImage.launch("image/*")
        }

        saveButton?.setOnClickListener {
            name = nameEditTextView?.text.toString()


            Model.instance.updateUser(name!!, uri!!) {
                afterEditVisibility()
            }

        }


    }

    fun editClickVisibility() {
        nameTextView?.visibility = View.INVISIBLE
        descriptionTextView?.visibility = View.INVISIBLE
        emailTextView?.visibility = View.INVISIBLE
        nameEditTextView?.visibility = View.VISIBLE
        saveButton?.visibility = View.VISIBLE
        photoButton?.visibility = View.VISIBLE
        nameEditTextView?.text = name
    }

    fun afterEditVisibility() {
        nameTextView?.visibility = View.VISIBLE
        // descriptionTextView?.visibility=View.VISIBLE
        emailTextView?.visibility = View.VISIBLE
        nameEditTextView?.visibility = View.INVISIBLE
        saveButton?.visibility = View.GONE
        photoButton?.visibility = View.GONE
        nameTextView?.text = name
    }


}