package com.example.greenapp.models

import androidx.room.Entity

@Entity(tableName = "myTips")
class MyTip(
    id: String = "",
    description: String = "",
    addedAt: Long = 0,
    dislikes: Int = 0,
    lastUpdated: Long = System.currentTimeMillis(),
) : Tip(id, description, addedAt, dislikes, lastUpdated) {
    companion object {
        fun fromTip(tip: Tip): MyTip {
            return MyTip(
                tip.id,
                tip.description,
                tip.addedAt,
                tip.dislikes,
                tip.lastUpdated,
            )
        }
    }
    constructor() : this("", "", 0, 0, 0)
}