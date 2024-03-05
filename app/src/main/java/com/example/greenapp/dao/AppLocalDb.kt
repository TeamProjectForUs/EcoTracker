package com.example.greenapp.dao



import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.greenapp.Model.User
import com.example.greenapp.base.MyApplication
import com.example.greenapp.Model.Post
import com.example.greenapp.Model.Tip


@Database(entities = [Post::class, Tip::class], version = 8)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun tipsDao(): TipsDao
}

object AppLocalDatabase {

    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "greenapp.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}