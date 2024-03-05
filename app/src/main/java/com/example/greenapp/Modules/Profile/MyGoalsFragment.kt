package com.example.greenapp.Modules.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenapp.BaseMenuProfileFragment
import com.example.greenapp.adapters.GoalsAdapter
import com.example.greenapp.databinding.FragmentMyGoalsBinding


class MyGoalsFragment : BaseMenuProfileFragment() {


    private var _binding: FragmentMyGoalsBinding? = null
    private val binding: FragmentMyGoalsBinding get() = _binding!!


    private lateinit var adapter: GoalsAdapter
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyGoalsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        val sharedVm = getSharedViewModel()

        adapter = GoalsAdapter(
            listOf(),
            onCompleteGoal = sharedVm::completeGoal,
            onPublishGoal = sharedVm::publishGoal,
            onRemoveGoal = sharedVm::removeGoal
        )


        sharedVm.currentUser.observe(viewLifecycleOwner) { user ->
            adapter.setGoals(user.goals)
        }

        binding.rvGoals.adapter = adapter
    }

}