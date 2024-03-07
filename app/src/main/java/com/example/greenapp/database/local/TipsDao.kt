package com.example.greenapp.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.greenapp.models.Tip

@Dao
interface TipsDao {
    @Query("SELECT * FROM Tip")
    fun getAllTips(): LiveData<List<Tip>>
    @Delete
    suspend fun deleteTip(tip: Tip)

    @Insert(onConflict = REPLACE)
    fun addTip(tip: Tip)

    @Insert(onConflict = REPLACE)
    fun addTips(tips: List<Tip>)

}