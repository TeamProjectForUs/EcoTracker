package com.example.greenapp.models

import androidx.room.Entity
@Entity(tableName = "otherUsers")
class OtherUser(
    name: String,
    id: String,
    email: String,
    password: String,
    uri: String = IMAGE_DEFAULT,
    bio: String = "No bio",
    friends: MutableList<String> = mutableListOf(),
    tipDislikeList: MutableList<String> = mutableListOf(),
    currentLikeList: MutableList<String> = mutableListOf(),
    goals: MutableList<Goal> = mutableListOf(),
    isChecked: Boolean,
) : User(
    name, id, email, password, uri, bio, friends, tipDislikeList, currentLikeList, goals, isChecked
) {
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
}