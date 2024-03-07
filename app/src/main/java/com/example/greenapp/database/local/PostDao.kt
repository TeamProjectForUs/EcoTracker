package com.example.greenapp.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.greenapp.models.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    fun getAll(): LiveData<MutableList<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg posts: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Post>)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM Post WHERE postUid =:postUid")
    fun getPostById(postUid: String): LiveData<MutableList<Post>>
}