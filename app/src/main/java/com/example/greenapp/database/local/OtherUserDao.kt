package com.example.greenapp.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.greenapp.models.OtherUser

@Dao
interface OtherUserDao {
    @Query("SELECT * FROM otherUsers")
    fun getAllOtherUsersLive(): LiveData<List<OtherUser>>

    @Query("SELECT * FROM otherUsers")
    fun getAllOtherUsers(): List<OtherUser>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: OtherUser)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<OtherUser>)
    @Delete
    fun delete(user: OtherUser)
}