package com.example.greenapp

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.User
import com.example.greenapp.Modules.Posts.PostFullViewFragmentArgs
import com.squareup.picasso.Picasso

class ProfileViewFragment : BaseMenuFragment() {

    private val args: PostFullViewFragmentArgs by navArgs()
    private var imageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var nameEditTextView: TextView? = null
    private var descriptionEditTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var name: String? = null
    private var description: String? = null
    private var uri: String? = null
    private var email: String? = null
    private var Userid: String? = null
    private var editButton: Button? = null
    private var cancelButton: Button? = null
    private var saveButton: Button? = null
    private var photoButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile_view, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {


        imageView = view.findViewById(R.id.image)
        nameTextView = view.findViewById((R.id.profileName))
//        descriptionTextView=view.findViewById(R.id.profileDescription)
        editButton = view.findViewById(R.id.btnEdit)
        nameEditTextView = view.findViewById(R.id.profileNameEdit)
//        descriptionEditTextView=view.findViewById(R.id.profileDescriptionEdit)
        emailTextView = view.findViewById(R.id.profileEmail)
        cancelButton = view.findViewById(R.id.btnCancel)
        saveButton = view.findViewById(R.id.btnSave)
        photoButton = view.findViewById(R.id.btnProfilePhoto)



        getSharedViewModel()
            .currentUser
            .observe(viewLifecycleOwner) { user ->
                Toast.makeText(context, " we got here.", Toast.LENGTH_SHORT).show()
                name = user.name
                email = user.email
                Userid = user.id
                uri = user.uri
                nameTextView?.text = name
                emailTextView?.text = email
                uri?.let {
                    if (it.isNotEmpty()) {
                        Picasso.get().load(it.toUri()).resize(1000, 1000).centerInside()
                            .into(imageView)
                    }
                }
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



        editButton?.setOnClickListener {
            editClickVisibility()

        }
        cancelButton?.setOnClickListener {
            afterEditVisibility()
        }
        saveButton?.setOnClickListener {
            name = nameEditTextView?.text.toString()


            Model.instance.updateUser(name!!, uri!!) {
                afterEditVisibility()
            }

        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun editClickVisibility() {
        nameTextView?.visibility = View.INVISIBLE
        descriptionTextView?.visibility = View.INVISIBLE
        emailTextView?.visibility = View.INVISIBLE
        nameEditTextView?.visibility = View.VISIBLE
//            descriptionEditTextView?.visibility=View.VISIBLE
        cancelButton?.visibility = View.VISIBLE
        saveButton?.visibility = View.VISIBLE
        photoButton?.visibility = View.VISIBLE
        editButton?.visibility = View.GONE
        nameEditTextView?.text = name
        // descriptionEditTextView?.text=description

    }

    fun afterEditVisibility() {
        nameTextView?.visibility = View.VISIBLE
        // descriptionTextView?.visibility=View.VISIBLE
        emailTextView?.visibility = View.VISIBLE
        nameEditTextView?.visibility = View.INVISIBLE
        cancelButton?.visibility = View.GONE
        saveButton?.visibility = View.GONE
        photoButton?.visibility = View.GONE
        editButton?.visibility = View.VISIBLE
        nameTextView?.text = name
        emailTextView?.text = email
    }


}