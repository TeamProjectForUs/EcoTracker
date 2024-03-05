package com.example.greenapp.Model

import android.content.Context
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.greenapp.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.UUID

@Entity
data class Post(
    val name: String,
    val id: String,
    var userUri: String = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg",
    var uri: String = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg",
    val description: String,
    var isChecked: Boolean,
    @PrimaryKey var postUid: String,
    var lastUpdated: Long? = null,
) {

    fun isDefaultImage(): Boolean {
        return uri == "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg"
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
        const val ID_KEY = "id"
        const val URI_KEY = "uri"
        const val DESCRIPTION_KEY = "description"
        const val IS_CHECKED_KEY = "isChecked"
        const val POSTUID_KEY = "postUid"
        const val USER_URI_KEY = "userUri"

        const val LAST_UPDATED = "lastUpdated"
        const val GET_LAST_UPDATED = "get_last_updated"

        fun fromJSON(json: Map<String, Any>): Post {
            val name = json[NAME_KEY] as? String ?: ""
            val id = json[ID_KEY] as? String ?: ""
            val uri = json[URI_KEY] as? String ?: ""
            val des = json[DESCRIPTION_KEY] as? String ?: ""
            val isChecked = json[IS_CHECKED_KEY] as? Boolean ?: false
            val postUid = json[POSTUID_KEY] as? String ?: ""
            val userUri = json[USER_URI_KEY] as? String ?: ""

            val post = Post(name, id, userUri = userUri, uri, des, isChecked, postUid)
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
                ID_KEY to id,
                URI_KEY to uri,
                USER_URI_KEY to userUri,
                DESCRIPTION_KEY to description,
                IS_CHECKED_KEY to isChecked,
                POSTUID_KEY to postUid,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
        }
}
