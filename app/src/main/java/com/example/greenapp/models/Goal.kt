package com.example.greenapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String,
    val tip: Tip,
    var done: Boolean,
) {
    constructor() : this("",Tip(), false)

    override fun equals(other: Any?): Boolean {
        return other is Goal && other.tip.id == tip.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + tip.hashCode()
        result = 31 * result + done.hashCode()
        return result
    }
}

