package com.example.ecotracker.dao;

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecotracker.model.User


@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM User WHERE name =:name")
    fun getUserByName(name: String): User
}