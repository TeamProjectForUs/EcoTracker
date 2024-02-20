package com.example.ecotracker.model;

import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.ecotracker.dao.AppLocalDatabase
import java.util.concurrent.Executors

class Model private constructor() {

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object {
        val instance: Model = Model()
    }

    interface GetAllUsersListener {
        fun onComplete(users: List<User>)
    }

    fun addUser(user: User, callback: () -> Unit) {
        executor.execute {
            database.userDao().insert(user)
            mainHandler.post {
                callback()
            }
        }
    }
}