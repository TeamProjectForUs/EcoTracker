package com.example.greenapp.Modules.Posts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.greenapp.BaseFragment
import com.example.greenapp.Model.Model
import com.example.greenapp.R
import com.squareup.picasso.Picasso


class PostFullViewFragment : BaseFragment() {
    private val args: PostFullViewFragmentArgs by navArgs()
    private var imageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var nameEditTextView: TextView? = null
    private var descriptionEditTextView: TextView? = null
    private var postUid: String? = null
    private var postUri: String? = null
    private var postUserid: String? = null
    private var editButton: Button? = null
    private var cancelButton: Button? = null
    private var saveButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_post_full_view, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {


        imageView = view.findViewById(R.id.image)
        nameTextView = view.findViewById((R.id.name))
        descriptionTextView = view.findViewById(R.id.description)
        editButton = view.findViewById(R.id.btnEdit)
        nameEditTextView = view.findViewById(R.id.nameEdit)
        descriptionEditTextView = view.findViewById(R.id.descriptionEdit)
        cancelButton = view.findViewById(R.id.btnCancel)
        saveButton = view.findViewById(R.id.btnSave)

        postUid = args.postUid
        postUserid = args.postUserId
        postUri = args.postImageUri
        nameTextView?.text = args.postName
        descriptionTextView?.text = args.postDes
        Picasso.get().load(args.postImageUri).resize(1000, 1000).centerInside().into(imageView)


        getSharedViewModel().currentUser.observe(viewLifecycleOwner) { user ->
            if (postUserid.equals(user.id)) {
                editButton?.visibility = View.VISIBLE
            }
            getSharedViewModel().currentUser.removeObservers(viewLifecycleOwner)

        }
        editButton?.setOnClickListener {
            nameTextView?.visibility = View.INVISIBLE
            descriptionTextView?.visibility = View.INVISIBLE
            nameEditTextView?.visibility = View.VISIBLE
            descriptionEditTextView?.visibility = View.VISIBLE
            cancelButton?.visibility = View.VISIBLE
            saveButton?.visibility = View.VISIBLE
            editButton?.visibility = View.GONE

            nameEditTextView?.text = args.postName
            descriptionEditTextView?.text = args.postDes

        }
        cancelButton?.setOnClickListener {
            nameTextView?.visibility = View.VISIBLE
            descriptionTextView?.visibility = View.VISIBLE
            nameEditTextView?.visibility = View.INVISIBLE
            descriptionEditTextView?.visibility = View.INVISIBLE
            cancelButton?.visibility = View.INVISIBLE
            saveButton?.visibility = View.INVISIBLE
            editButton?.visibility = View.VISIBLE


        }
        saveButton?.setOnClickListener {
            val name = nameEditTextView?.text.toString()
            val des = descriptionEditTextView?.text.toString()

            Model.instance.updatePost(postUid!!, name, des, postUri!!) {
                Toast.makeText(context, " post updated.", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view)
                    .navigate(R.id.action_postFullViewFragment_to_feedFragment)
            }
        }
//        val pickImage= registerForActivityResult(ActivityResultContracts.GetContent()) {
//            Picasso.get().load(it).resize(1000, 1000).centerInside().into(imageField)
//            if (it != null) {
//                uri = it
//            }
//        }
//        photoButton?.setOnClickListener{
//            pickImage.launch("image/*")
//        }

    }


}