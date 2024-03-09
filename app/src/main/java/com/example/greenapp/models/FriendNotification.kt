package com.example.greenapp.models

data class FriendNotification(
    val friendId: String,
    var seen: Boolean
) {
    constructor()  : this("", false)
}