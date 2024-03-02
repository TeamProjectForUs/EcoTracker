package com.example.greenapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
data class Tip(
    @PrimaryKey var id:String,
    val description: String,
    var lastUpdated: Long = System.currentTimeMillis()
    ) {
    companion object {
        var lastUpdated: Long = 0
        const val ID_KEY="id"
        const val DESCRIPTION_KEY="description"
        fun fromJSON(json: Map<String, Any>): Tip {
            val id = json[Post.ID_KEY] as? String ?: ""
            val des = json[Post.DESCRIPTION_KEY] as? String ?: ""
            var tip = Tip(id,des)
            return tip
        }
    }
    val json: Map<String, Any>
        get() {
            return hashMapOf(
                Post.ID_KEY to id,
                Post.DESCRIPTION_KEY to description,
            )
        }

}