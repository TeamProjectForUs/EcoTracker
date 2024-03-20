package com.example.greenapp.database.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.greenapp.models.Goal

@Dao
interface GoalsDao {
    @Query("SELECT * FROM goals")
    fun getAllGoalsLive(): LiveData<List<Goal>>
    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<Goal>

    @Query("DELETE FROM goals")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Insert(onConflict = REPLACE)
    suspend fun addGoal(goal: Goal)

    @Insert(onConflict = REPLACE)
    suspend fun addGoals(goals: List<Goal>)
}