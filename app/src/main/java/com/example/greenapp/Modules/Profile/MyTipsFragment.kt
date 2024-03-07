package com.example.greenapp.modules.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenapp.BaseMenuProfileFragment
import com.example.greenapp.modules.Tips.createTipsAdapter
import com.example.greenapp.adapters.TipsAdapter
import com.example.greenapp.databinding.FragmentMyTipsBinding


class MyTipsFragment : BaseMenuProfileFragment() {

    private var _binding: FragmentMyTipsBinding? = null
    private val binding: FragmentMyTipsBinding get() = _binding!!


    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMyTipsBinding.inflate(inflater)
        return binding.root
    }


    private lateinit var adapter: TipsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(requireParentFragment())[ProfileViewModel::class.java]
        val sharedVm = getSharedViewModel()

        adapter =
            createTipsAdapter(
                TipsAdapter.TipAdapterData.AllTips(listOf()),
                profileViewModel,
                isLikedPage = true,
                onMakeGoal = { tip, position ->
                    sharedVm.currentUser.value?.let { user ->
                        profileViewModel.toggleGoalTip(user.goals, tip)
                        adapter.updateUserGoalList(user.goals, position)
                    }
                }
            )


        binding.rvTips.adapter = adapter
        binding.rvTips.layoutManager = LinearLayoutManager(requireContext())

        sharedVm.currentUser.value?.let { user ->
            profileViewModel.startListeningMyTips(user.currentLikeList)
            profileViewModel.myTips.observe(viewLifecycleOwner) { likedTips ->
                adapter.setTipsData(TipsAdapter.TipAdapterData.AllTips(likedTips))
            }
        }

        binding.progressBar.visibility = VISIBLE
        profileViewModel.myTipsLoadingState.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility = GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}