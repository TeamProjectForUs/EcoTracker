package com.example.greenapp.Model

import com.example.greenapp.Model.User.Companion.IMAGE_DEFAULT

data class UserModelFirebase(
    val name: String,
    val id: String,
    val email: String,
    val password: String,
    val uri: String = IMAGE_DEFAULT,
    val bio: String = "No bio",
    val friends: MutableList<String> = mutableListOf(),
    val tipDislikeList: MutableList<String> = mutableListOf(),
    val currentLikeList: MutableList<String> = mutableListOf(),
    val goals: MutableList<Goal> = mutableListOf(),
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
        false
    )


    companion object {
        const val LIKE_KEY = "currentLikeList"
        const val GOALS_KEY = "goals"
        const val FRIENDS_KEY = "friends"
        const val BIO_KEY = "bio"

    }

}