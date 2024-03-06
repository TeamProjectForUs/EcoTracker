package com.example.greenapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenapp.Model.Model
import com.example.greenapp.adapters.UsersAdapter
import com.example.greenapp.databinding.FragmentSearchBinding
import com.google.api.Distribution.BucketOptions.Linear
import com.google.gson.Gson

class UserSearchFragment : BaseMenuFragment() {


    private lateinit var userSearchViewModel: UserSearchViewModel

    var _binding: FragmentSearchBinding? = null
    val binding: FragmentSearchBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userSearchViewModel = ViewModelProvider(this)[UserSearchViewModel::class.java]
        val adapter = UsersAdapter(listOf()) { user ->
            // move to other user profile screen
            val act = UserSearchFragmentDirections.actionGlobalProfileViewFragment(
                Gson().toJson(user)
            )
            findNavController().navigate(act)
        }

        binding.etUserSearch.addTextChangedListener(
            onTextChanged = { editable, start, before, count ->
                userSearchViewModel.searchUsers(editable.toString())
            }
        )

        userSearchViewModel.userResults.observe(viewLifecycleOwner) { users ->
            adapter.setUsers(users)
            if (users.size > 0)
                Log.d("Someuser", users[0].toString())
        }

        userSearchViewModel.userLoadingState.observe(viewLifecycleOwner) { loadingState ->
            if (loadingState == null) return@observe
            when (loadingState) {
                Model.LoadingState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                Model.LoadingState.LOADED -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}