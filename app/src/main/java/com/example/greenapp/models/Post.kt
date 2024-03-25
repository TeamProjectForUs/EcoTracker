package com.example.greenapp.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.greenapp.models.User.Companion.IMAGE_DEFAULT
import com.example.greenapp.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
data class Post(
    var name: String,
    var userUri: String = IMAGE_DEFAULT,
    var uri: String = IMAGE_DEFAULT,
    val description: String,
    val userId: String,
    val datePosted: Long = System.currentTimeMillis(),
    var isChecked: Boolean,
    @PrimaryKey var postUid: String,
    var lastUpdated: Long? = null,
) {

    fun isDefaultImage(): Boolean {
        return uri == IMAGE_DEFAULT
    }

    companion object {

        var lastUpdated: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(GET_LAST_UPDATED, value)?.apply()
            }


        const val NAME_KEY = "name"
        const val USERID_KEY = "userId"
        const val URI_KEY = "uri"
        const val DESCRIPTION_KEY = "description"
        const val IS_CHECKED_KEY = "isChecked"
        const val POSTUID_KEY = "postUid"
        const val DATE_POSTED = "datePosted"

        const val USER_URI_KEY = "userUri"

        const val LAST_UPDATED = "lastUpdated"
        const val GET_LAST_UPDATED = "get_last_updated"

        fun fromJSON(json: Map<String, Any>): Post {
            val name = json[NAME_KEY] as? String ?: ""
            val userId = json[USERID_KEY] as? String ?: ""
            val uri = json[URI_KEY] as? String ?: ""
            val des = json[DESCRIPTION_KEY] as? String ?: ""
            val isChecked = json[IS_CHECKED_KEY] as? Boolean ?: false
            val postUid = json[POSTUID_KEY] as? String ?: ""
            val userUri = json[USER_URI_KEY] as? String ?: ""
            val datePosted = json[DATE_POSTED] as? Long ?: 0

            val post = Post(
                name = name,
                userId = userId,
                userUri = userUri,
                uri = uri,
                description = des,
                isChecked = isChecked,
                postUid = postUid,
                datePosted = datePosted
            )
            val timestamp: Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                post.lastUpdated = it.seconds
            }
            return post
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                NAME_KEY to name,
                USERID_KEY to userId,
                DATE_POSTED to datePosted,
                URI_KEY to uri,
                USER_URI_KEY to userUri,
                DESCRIPTION_KEY to description,
                IS_CHECKED_KEY to isChecked,
                POSTUID_KEY to postUid,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
        }
}
