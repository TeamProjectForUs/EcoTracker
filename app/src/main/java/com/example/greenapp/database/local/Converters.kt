package com.example.greenapp.database.local

import androidx.room.TypeConverter
import com.example.greenapp.models.FriendNotification
import com.example.greenapp.models.Goal
import com.example.greenapp.models.Tip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTip(tip: Tip): String {
        return gson.toJson(tip)
    }

    @TypeConverter
    fun toTip(tipString: String): Tip {
        return gson.fromJson(tipString, Tip::class.java)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromFriendRequestList(value: List<FriendNotification>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFriendRequestList(friendNotificationsString: String): List<FriendNotification> {
        val listType = object : TypeToken<List<FriendNotification>>() {}.type
        return gson.fromJson(friendNotificationsString, listType)
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromGoalList(value: List<Goal>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toGoalList(value: String): List<Goal>? {
        val listType = object : TypeToken<List<Goal>>() {}.type
        return gson.fromJson(value, listType)
    }
}
