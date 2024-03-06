package com.example.greenapp

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.greenapp.Model.Model
import com.example.greenapp.Model.User
import com.example.greenapp.Model.UserModelFirebase
import com.example.greenapp.Modules.Posts.PostFullViewFragmentArgs
import com.example.greenapp.databinding.FragmentMyTipsBinding
import com.example.greenapp.databinding.FragmentProfileOtherBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ProfileViewFragment : BaseMenuFragment() {

    private val args: ProfileViewFragmentArgs by navArgs()

    private var _binding: FragmentProfileOtherBinding? = null
    private val binding: FragmentProfileOtherBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileOtherBinding.inflate(inflater)
        return binding.root
    }

    private var colorHighlight: Int = 0
    private var colorNormal: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorHighlight = ContextCompat.getColor(requireContext(), R.color.strockGreenLight)
        colorNormal = ContextCompat.getColor(requireContext(), R.color.white)
        val userString = args.user
        val user = Gson().fromJson(userString, UserModelFirebase::class.java)
        val sharedVm = getSharedViewModel()

        sharedVm.currentUser.observe(viewLifecycleOwner) { currentUser ->

            if (currentUser.friends.contains(user.id)) {
                binding.onlyFriendsLayout.visibility = View.VISIBLE
                ViewCompat.setBackgroundTintList(
                    binding.addFriendBbtn,
                    ColorStateList.valueOf(colorHighlight)
                )
                binding.followingStatus.text = getString(R.string.following)
                binding.addFriendBbtn.text = getString(R.string.unfollow)
            } else {
                binding.onlyFriendsLayout.visibility = View.GONE
                ViewCompat.setBackgroundTintList(
                    binding.addFriendBbtn,
                    ColorStateList.valueOf(colorNormal)
                )
                binding.addFriendBbtn.text = getString(R.string.follow)
                binding.followingStatus.text = getString(R.string.not_following)
            }
        }

        binding.onlyFriendsLayout.visibility = View.GONE
        binding.addFriendBbtn.setOnClickListener {
            val currentUserList = sharedVm.currentUser.value?.friends ?: mutableListOf()
            sharedVm.toggleFriend(user, currentUserList)
        }

        binding.profileName.text = user.name
        binding.bioArea.text = user.bio


        Picasso.get()
            .load(user.getImage().toUri())
            .resize(1000, 1000)
            .centerInside()
            .into(binding.profileImageViewOther)
    }


}