package com.example.greenapp.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.greenapp.models.MyTip

@Dao
interface MyTipsDao {
    @Query("SELECT * FROM mytips")
    fun getMyTips(): LiveData<List<MyTip>>

    @Query("DELETE FROM myTips")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteMyTip(tip: MyTip)
    @Insert(onConflict = REPLACE)
    fun addMyTip(tip: MyTip)
    @Insert(onConflict = REPLACE)
    fun addMyTips(tips: List<MyTip>)
}