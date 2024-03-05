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
import com.example.greenapp.SharedViewModel
import com.example.greenapp.databinding.FragmentPostFullViewBinding
import com.squareup.picasso.Picasso


class PostFullViewFragment : BaseFragment() {
    private val args: PostFullViewFragmentArgs by navArgs()

    private var postUid: String? = null
    private var postUri: String? = null
    private var postUserid: String? = null


    private var _binding: FragmentPostFullViewBinding? = null
    private val binding: FragmentPostFullViewBinding get() = _binding!!

    private lateinit var viewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostFullViewBinding.inflate(inflater)
        val view = binding.root
        setupUI(view)
        viewModel = getSharedViewModel()
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupUI(view: View) {

        postUid = args.postUid
        postUserid = args.postUserId
        postUri = args.postImageUri
        binding.name.text = args.postName
        binding.description.text = args.postDes
        Picasso.get()
            .load(args.postImageUri)
            .resize(1000, 1000)
            .centerInside()
            .into(binding.image)


        getSharedViewModel().currentUser.observe(viewLifecycleOwner) { user ->
            if (postUserid.equals(user.id)) {
                binding.btnEdit.visibility = View.VISIBLE
            }
            getSharedViewModel().currentUser.removeObservers(viewLifecycleOwner)

        }
        binding.btnEdit.setOnClickListener {
            binding.name.visibility = View.INVISIBLE
            binding.description.visibility = View.INVISIBLE
            binding.nameEdit.visibility = View.VISIBLE
            binding.descriptionEdit.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE
            binding.btnEdit.visibility = View.GONE
            binding.name.text = args.postName
            binding.description.text = args.postDes
        }
        binding.btnCancel.setOnClickListener {
            binding.name.visibility = View.VISIBLE
            binding.description.visibility = View.VISIBLE
            binding.nameEdit.visibility = View.INVISIBLE
            binding.descriptionEdit.visibility = View.INVISIBLE
            binding.btnCancel.visibility = View.INVISIBLE
            binding.btnSave.visibility = View.INVISIBLE
            binding.btnEdit.visibility = View.VISIBLE
        }
        binding.btnSave.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val des = binding.descriptionEdit.text.toString()
            viewModel.updatePost(postUid!!, name, des, postUri!!) {
                Toast.makeText(context, " post updated.", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view)
                    .navigate(R.id.action_postFullViewFragment_to_feedFragment)
            }
        }

    }


}