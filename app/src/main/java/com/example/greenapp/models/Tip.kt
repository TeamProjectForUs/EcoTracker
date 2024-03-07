package com.example.greenapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class Tip(
    @PrimaryKey var id: String = "",
    var description: String = "",
    var addedAt: Long = 0,
    var dislikes: Int = 0,
    var lastUpdated: Long = System.currentTimeMillis(),
) {

    constructor() : this("", "", 0, 0, 0)

    companion object {
        var lastUpdated: Long = 0
        const val ID_KEY = "id"
        const val ADDED_AT = "addedAt"
        const val DISLIKES = "dislikes"
        const val DESCRIPTION_KEY = "description"
        fun fromJSON(json: Map<String, Any>): Tip {
            val id = json[ID_KEY] as? String ?: ""
            val des = json[DESCRIPTION_KEY] as? String ?: ""
            val dislikes = (json[DISLIKES] as? Int ?: 0).toInt()
            val addedAt = (json[ADDED_AT] as? Long ?: 0)
            return Tip(id = id, description = des, addedAt = addedAt, dislikes = dislikes)
        }

        fun toJson(tip: Tip): Map<String, Any> {
            return hashMapOf(
                ID_KEY to tip.id,
                DESCRIPTION_KEY to tip.description,
                ADDED_AT to tip.addedAt,
                DISLIKES to tip.dislikes,
            )
        }
    }

    fun isNew(diffRate: Long = 7 * 24 * 60 * 60 * 1000): Boolean {
        val timeNow = System.currentTimeMillis()
        val diff = timeNow - addedAt
        return diff < diffRate
    }
}