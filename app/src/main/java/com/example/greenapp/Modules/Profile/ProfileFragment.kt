package com.example.greenapp.modules.Profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.greenapp.BaseMenuProfileFragment
import com.example.greenapp.modules.Posts.AddPostFragment
import com.example.greenapp.modules.Tips.TipsViewModel
import com.example.greenapp.R
import com.example.greenapp.databinding.FragmentProfileBinding
import com.google.android.material.button.MaterialButton


sealed class ProfileTab {
    data object AllPosts : ProfileTab()
    data object Photos : ProfileTab()
    data object FavoriteTips : ProfileTab()
    data object Goals : ProfileTab()
    data object AddPost : ProfileTab()
}

class ProfileFragment : BaseMenuProfileFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var tipsViewModel: TipsViewModel
    private lateinit var profileTab: ProfileTab

    private lateinit var allPostsBtn: MaterialButton
    private lateinit var photosBtn: MaterialButton
    private lateinit var favoritesBtn: MaterialButton
    private lateinit var goalsBtn: MaterialButton
    private lateinit var addPostBtn: ImageView

    private var colorHighlight: Int = 0
    private var colorNormal: Int = 0


    private var fragmentAllPosts: MyPostsFragment? = null
    private var fragmentPhotos: MyPhotosFragment? = null
    private var fragmentTips: MyTipsFragment? = null
    private var fragmentGoals: MyGoalsFragment? = null
    private var fragmentAddPost: AddPostFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        profileTab = ProfileTab.AllPosts
        _binding = FragmentProfileBinding.inflate(inflater)

        colorHighlight = ContextCompat.getColor(requireContext(), R.color.strockGreenLight)
        colorNormal = ContextCompat.getColor(requireContext(), R.color.white)
        return binding.root
    }

    fun updateTabs(profileTab: ProfileTab) {
        this.profileTab = profileTab
        ViewCompat.setBackgroundTintList(allPostsBtn, ColorStateList.valueOf(colorNormal))
        ViewCompat.setBackgroundTintList(photosBtn, ColorStateList.valueOf(colorNormal))
        ViewCompat.setBackgroundTintList(favoritesBtn, ColorStateList.valueOf(colorNormal))
        ViewCompat.setBackgroundTintList(goalsBtn, ColorStateList.valueOf(colorNormal))

        when (profileTab) {
            is ProfileTab.AllPosts -> {
                ViewCompat.setBackgroundTintList(
                    allPostsBtn,
                    ColorStateList.valueOf(colorHighlight)
                )
            }

            is ProfileTab.Photos -> {
                ViewCompat.setBackgroundTintList(photosBtn, ColorStateList.valueOf(colorHighlight))
            }

            is ProfileTab.FavoriteTips -> {
                ViewCompat.setBackgroundTintList(
                    favoritesBtn,
                    ColorStateList.valueOf(colorHighlight)
                )
            }

            is ProfileTab.Goals -> {
                ViewCompat.setBackgroundTintList(goalsBtn, ColorStateList.valueOf(colorHighlight))
            }

            is ProfileTab.AddPost -> {

            }
        }

        val fragment = getFragment(profileTab)
        childFragmentManager.beginTransaction()
            .replace(R.id.profileContentFragment, fragment)
            .commit()
    }

    private fun getFragment(profileTab: ProfileTab): Fragment {
        return when (profileTab) {
            is ProfileTab.AllPosts -> {
                if (fragmentAllPosts == null) {
                    fragmentAllPosts = MyPostsFragment()
                }
                fragmentAllPosts!!
            }

            is ProfileTab.Photos -> {
                if (fragmentPhotos == null) {
                    fragmentPhotos = MyPhotosFragment()
                }
                fragmentPhotos!!
            }

            is ProfileTab.FavoriteTips -> {
                if (fragmentTips == null) {
                    fragmentTips = MyTipsFragment()
                }
                fragmentTips!!
            }

            is ProfileTab.Goals -> {
                if (fragmentGoals == null) {
                    fragmentGoals = MyGoalsFragment()
                }
                fragmentGoals!!
            }

            is ProfileTab.AddPost -> {
                if (fragmentAddPost == null) {
                    fragmentAddPost = AddPostFragment()
                }
                fragmentAddPost!!
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val sharedVm = getSharedViewModel()
        sharedVm.currentUser
            .observe(viewLifecycleOwner) { user ->
                user?.let { user ->
                    binding.userNameTv.text = user.name
                    binding.bioTv.text = user.bio
                }
            }

        allPostsBtn = binding.allPostsBtn
        photosBtn = binding.photosBtn
        favoritesBtn = binding.favEcoTipsBtn
        goalsBtn = binding.goalsBbtn
        addPostBtn = binding.addPostBtn

        createNotificationsMenu(binding.notificationsBtn)


        allPostsBtn.setOnClickListener {
            updateTabs(ProfileTab.AllPosts)
        }
        photosBtn.setOnClickListener {
            updateTabs(ProfileTab.Photos)
        }
        favoritesBtn.setOnClickListener {
            updateTabs(ProfileTab.FavoriteTips)
        }
        goalsBtn.setOnClickListener {
            updateTabs(ProfileTab.Goals)
        }
        addPostBtn.setOnClickListener {
            updateTabs(ProfileTab.AddPost)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.profileContentFragment, getFragment(ProfileTab.AllPosts))
            .commit()

        Navigation.createNavigateOnClickListener(R.id.action_myPostsFragment_to_addPostFragment)
    }


}