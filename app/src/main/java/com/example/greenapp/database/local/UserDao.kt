package com.example.greenapp.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.greenapp.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrent(): LiveData<User?>
    @Query("DELETE FROM users")
    fun deleteAllUsers()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: User)

    @Delete
    fun delete(user: User)
}