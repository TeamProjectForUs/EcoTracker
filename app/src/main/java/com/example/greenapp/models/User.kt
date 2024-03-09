package com.example.greenapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
open class User(
    var name: String,
    @PrimaryKey var id: String,
    var email: String,
    var password: String,
    var uri: String = IMAGE_DEFAULT,
    var bio: String = "No bio",
    var friendNotifications: MutableList<FriendNotification> = mutableListOf(),
    var friends: MutableList<String> = mutableListOf(),
    var tipDislikeList: MutableList<String> = mutableListOf(),
    var currentLikeList: MutableList<String> = mutableListOf(),
    var goals: MutableList<Goal> = mutableListOf(),
    var isChecked: Boolean,
) {
    fun getImage(): String {
        if (uri.trim().isEmpty())
            return IMAGE_DEFAULT
        return uri
    }

    override fun toString(): String {
        return "UserModelFirebase(name='$name', id='$id', email='$email', password='$password', uri='$uri', tipDislikeList=$tipDislikeList, currentLikeList=$currentLikeList, goals=$goals, isChecked=$isChecked)"
    }

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "No bio",
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        false
    )

    companion object {
        const val LIKE_KEY = "currentLikeList"
        const val GOALS_KEY = "goals"
        const val FRIENDS_KEY = "friends"
        const val FRIEND_REQUESTS_KEY = "friendNotifications"
        const val BIO_KEY = "bio"
        const val IMAGE_DEFAULT =
            "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg"
        const val NAME_KEY = "name"
        const val ID_KEY = "id"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
        const val URI_KEY = "uri"
        const val IS_CHECKED_KEY = "isChecked"
        const val DISLIKE_LIST = "tipDislikeList"
    }

}