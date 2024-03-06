package com.example.greenapp.Model

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.greenapp.Model.UserModelFirebase.Companion.BIO_KEY
import com.google.gson.Gson

@Entity
data class User(
    val name: String,
    @PrimaryKey val id: String,
    val email: String,
    val password: String,
    val uri: String,
    val bio: String = "None",
    val friends: MutableList<String> = mutableListOf(),
    val goals: MutableList<Goal> = mutableListOf(),
    val tipDislikeList: MutableList<String> = mutableListOf(),
    val tipLikesList: MutableList<String> = mutableListOf(),
    var isChecked: Boolean,
) {

    companion object {
        const val IMAGE_DEFAULT =
            "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg"
        const val NAME_KEY = "name"
        const val ID_KEY = "id"
        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
        const val URI_KEY = "uri"
        const val IS_CHECKED_KEY = "isChecked"
        const val DISLIKE_LIST = "tipDislikeList"

        fun fromJSON(json: Map<String, Any>): User {
            val name = json[NAME_KEY] as? String ?: ""
            val id = json[ID_KEY] as? String ?: ""
            val email = json[EMAIL_KEY] as? String ?: ""
            val password = json[PASSWORD_KEY] as? String ?: ""
            val bio = json[BIO_KEY] as? String ?: ""
            val uri = json[URI_KEY] as? String ?: ""
            val isChecked = json[IS_CHECKED_KEY] as? Boolean ?: false
            return User(
                name,
                id,
                email,
                password,
                uri,
                bio = bio,
                isChecked = isChecked,
                friends = mutableListOf(),
                goals = mutableListOf(),
                tipLikesList = mutableListOf(),
                tipDislikeList = mutableListOf()
            )
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                NAME_KEY to name,
                ID_KEY to id,
                BIO_KEY to bio,
                EMAIL_KEY to email,
                PASSWORD_KEY to password,
                URI_KEY to uri,
                IS_CHECKED_KEY to isChecked
            )
        }
}