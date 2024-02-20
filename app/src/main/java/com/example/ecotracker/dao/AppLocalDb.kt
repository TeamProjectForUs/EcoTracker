package com.example.ecotracker.dao;



import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ecotracker.base.MyApplication
import com.example.ecotracker.model.User


@Database(entities = [User::class], version = 2)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDao
}

object AppLocalDatabase {

    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}