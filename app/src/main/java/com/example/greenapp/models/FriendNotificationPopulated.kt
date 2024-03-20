package com.example.greenapp.models

import android.util.Log
import com.example.greenapp.database.Model

data class FriendNotificationPopulated(
    var friend: User,
    var seen: Boolean,
) {
    companion object {
        fun fromNotifications(
            list: List<FriendNotification>,
            populatedListCallback: (List<FriendNotificationPopulated>) -> Unit,
        ) {

            Model.instance
                .getFireBaseModel()
                .getAllUsers { users ->
                    val map = mutableMapOf<String, User>()
                    val populated = mutableListOf<FriendNotificationPopulated>()
                    for (user in users) {
                        map[user.id] = user
                    }
                    for (notification in list) {
                        if (map.containsKey(notification.friendId)) {
                            populated.add(
                                FriendNotificationPopulated(
                                    map[notification.friendId]!!,
                                    notification.seen
                                )
                            )
                        }
                    }
                    populatedListCallback.invoke(populated)
                }
        }
    }
}