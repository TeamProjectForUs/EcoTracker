package com.example.greenapp.modules.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.greenapp.BaseFragment
import com.example.greenapp.BaseMenuFragment
import com.example.greenapp.R
import com.example.greenapp.adapters.AlertsAdapter
import com.example.greenapp.databinding.FragmentMyAlertsBinding
import com.example.greenapp.models.FriendNotificationPopulated
import com.example.greenapp.modules.Common.UserSearchFragmentDirections
import com.google.gson.Gson

class MyAlertsFragment : BaseMenuFragment() {

    var _binding: FragmentMyAlertsBinding? = null
    val binding: FragmentMyAlertsBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyAlertsBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedVm = getSharedViewModel()
        sharedVm.seeFriendNotifications()

        binding.rvAlerts.layoutManager = LinearLayoutManager(requireContext())
        sharedVm.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let { user ->
                FriendNotificationPopulated.fromNotifications(user.friendNotifications) { alerts ->
                    val adapter = AlertsAdapter(alerts,
                        onFriendClick = { notification ->
                            // navigate to friend fragment
                            // move to other user profile screen
                            val act = MyAlertsFragmentDirections.actionGlobalProfileViewFragment(
                                Gson().toJson(notification.friend)
                            )
                            findNavController().navigate(act)
                        },
                        onNotificationRemove = { notification ->
                            sharedVm.removeFriendNotification(notification.friend.id)
                        })
                    binding.rvAlerts.adapter = adapter
                }
            }

        }
    }
}

fun BaseFragment.createNotificationsMenu(
    view: ImageView
) {
    val sharedVm = getSharedViewModel()
    view.setOnClickListener {
        val menu = PopupMenu(requireContext(), it)
        menu.inflate(R.menu.alerts_menu)
        val friendRequests =
            sharedVm.unseenFriendNotifications.value?.size ?: 0
        menu.menu.getItem(0).setTitle("Friend Requests: (${friendRequests})")
        menu.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.friendsRequests) {
                findNavController().navigate(R.id.action_global_myAlerts)
            }
            true
        }
        menu.show()
    }
    sharedVm.unseenFriendNotifications.observe(viewLifecycleOwner) { unseenNotifications ->
        if (unseenNotifications.isNotEmpty()) {
            view.setImageResource(R.drawable.bell);
        } else {
            view.setImageResource(R.drawable.icon_notification)
        }
    }
}