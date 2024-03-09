package com.example.greenapp.modules.Profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import com.example.greenapp.BaseMenuFragment
import com.example.greenapp.database.Model
import com.example.greenapp.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso

class EditProfileFragment : BaseMenuFragment() {
    private var imageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var nameEditTextView: TextView? = null
    private var bioEditTextView: TextView? = null

    private var emailTextView: TextView? = null
    private var name: String? = null
    private var bio: String? = null
    private var uri: String? = null
    private var saveButton: Button? = null
    private var photoButton: MaterialButton? = null

    private var changePasswordBtn: MaterialButton? = null

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
        bioEditTextView = view.findViewById(R.id.bioId)
        saveButton = view.findViewById(R.id.editProfileSaveBtn)
        photoButton = view.findViewById(R.id.ChangeProfilePictureBtn)
        changePasswordBtn = view.findViewById(R.id.ChangePasswordBtn)



        bioEditTextView?.addTextChangedListener(
            afterTextChanged = {
                it?.let { editable ->
                    if (editable.length > 20) {
                        editable.delete(20, editable.length)
                    }
                }
            }
        )

        changePasswordBtn?.setOnClickListener {
            val changePasswordLayout =
                layoutInflater.inflate(R.layout.change_password_layout, null, false)

            val alert = AlertDialog.Builder(requireContext())
                .setTitle("EcoTracker")
                .setView(changePasswordLayout)
                .create()

            val okBtn = changePasswordLayout.findViewById<MaterialButton>(R.id.changePasswordBtnYes)
            val cancelBtn =
                changePasswordLayout.findViewById<MaterialButton>(R.id.changePasswordBtnNo)
            cancelBtn.setOnClickListener {
                alert.dismiss()
            }
            okBtn.setOnClickListener {
                // send the user a email to reset his password
                getSharedViewModel().currentUser.value?.let { user ->
                    Firebase.auth.sendPasswordResetEmail(user.email)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "We have sent you an email to reset your password. please check your inbox and your spam box",
                                Toast.LENGTH_SHORT
                            ).show()
                            alert.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            alert.dismiss()
                        }

                } ?: run {
                    Toast.makeText(
                        requireContext(),
                        "Unknown error occured, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            alert.show()
        }
        getSharedViewModel()
            .currentUser
            .observe(viewLifecycleOwner) { user ->
                user?.let { user ->
                    name = user.name
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

            }


        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            Picasso.get().load(it).resize(5000, 5000).centerInside().into(imageView)

            if (it != null) {
                uri = it.toString()
            }
        }

        createNotificationsMenu(view.findViewById(R.id.notificationsProfileBtn))
        photoButton?.setOnClickListener {
            pickImage.launch("image/*")
        }


        saveButton?.setOnClickListener {
            name = nameEditTextView?.text?.toString()
            bio = bioEditTextView?.text?.toString()
            if (name == null) {
                name = getSharedViewModel().currentUser.value?.name
            }
            if (bio == null) {
                bio = getSharedViewModel().currentUser.value?.bio
            }

            Model.instance.userRepository.updateUser(name, bio, uri) {
                Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
}