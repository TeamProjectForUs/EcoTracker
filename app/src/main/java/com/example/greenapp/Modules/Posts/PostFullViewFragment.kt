package com.example.greenapp.modules.Posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.greenapp.BaseFragment
import com.example.greenapp.R
import com.example.greenapp.modules.Common.SharedViewModel
import com.example.greenapp.databinding.FragmentPostFullViewBinding
import com.example.greenapp.models.User
import com.squareup.picasso.Picasso


class PostFullViewFragment : BaseFragment() {
    private val args: PostFullViewFragmentArgs by navArgs()

    private var postUid: String? = null
    private var postUri: String? = null
    private var postUserId: String? = null


    private var _binding: FragmentPostFullViewBinding? = null
    private val binding: FragmentPostFullViewBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostFullViewBinding.inflate(inflater)
        val view = binding.root
        setupUI(view)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupUI(view: View) {

        postUid = args.postUid
        postUserId = args.postUserId
        postUri = args.postImageUri
        binding.description.text = args.postDes


        if (!args.postImageUri.equals(User.IMAGE_DEFAULT)) {
            Picasso.get()
                .load(args.postImageUri)
                .resize(1000, 1000)
                .centerInside()
                .into(binding.image)

        } else {
            binding.image.visibility = View.GONE
        }

        getSharedViewModel()
            .currentUser
            .observe(viewLifecycleOwner) { user ->
                if (postUserId.equals(user?.id)) {
                    binding.btnEdit.visibility = View.VISIBLE
                } else {
                    binding.btnEdit.visibility = View.GONE
                }
            }

        binding.btnEdit.visibility = View.VISIBLE

        binding.btnEdit.setOnClickListener {
            binding.description.visibility = View.GONE
            binding.descriptionEdit.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
            binding.btnEdit.visibility = View.GONE
            binding.description.text = args.postDes
        }


        binding.btnCancel.setOnClickListener {
            binding.description.visibility = View.VISIBLE
            binding.descriptionEdit.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
            binding.btnSave.visibility = View.GONE
            binding.btnEdit.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {
            val des = binding.descriptionEdit.text.toString()
            getSharedViewModel()
                .updatePost(postUid!!, des, postUri!!) {
                Toast.makeText(context, " post updated.", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view)
                    .navigate(R.id.action_postFullViewFragment_to_feedFragment)
            }
        }

    }


}