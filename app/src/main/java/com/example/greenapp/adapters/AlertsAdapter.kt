package com.example.greenapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greenapp.databinding.AlertItemBinding
import com.example.greenapp.models.FriendNotificationPopulated

class AlertsAdapter(
    val friendNotifications: List<FriendNotificationPopulated>,
    val onFriendClick: (FriendNotificationPopulated) -> Unit,
    val onNotificationRemove: (FriendNotificationPopulated) -> Unit
) : RecyclerView.Adapter<AlertsAdapter.AlertsViewHolder>() {

    class AlertsViewHolder(
        val binding: AlertItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: FriendNotificationPopulated) {
            binding.alertDescriptionTv.text = "You got followed by ${notification.friend.name}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context))
        return AlertsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return friendNotifications.size
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val friend = friendNotifications[position]
        holder.bind(friend)
        holder.binding.alertDescriptionTv.setOnClickListener {
            onFriendClick.invoke(friend)
        }
        holder.binding.deleteAlertBtn.setOnClickListener {
            onNotificationRemove.invoke(friend)
        }
    }
}