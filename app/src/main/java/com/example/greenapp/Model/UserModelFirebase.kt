package com.example.greenapp.Model

data class UserModelFirebase(
    val name: String,
    val id: String,
    val email: String,
    val password: String,
    val uri: String,
    val tipDislikeList: MutableList<String> = mutableListOf(),
    val currentLikeList: MutableList<String> = mutableListOf(),
    val goals: MutableList<Goal> = mutableListOf(),
    var isChecked: Boolean,
) {

    constructor() : this(
        "", "", "", "", "",
        mutableListOf(), mutableListOf(), mutableListOf(), false
    )
    companion object {
        const val LIKE_KEY = "currentLikeList"
        const val GOALS_KEY = "goals"
    }

}