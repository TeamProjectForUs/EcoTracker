package com.example.greenapp.database.local


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.greenapp.models.Goal
import com.example.greenapp.models.MyTip
import com.example.greenapp.models.OtherUser
import com.example.greenapp.base.MyApplication
import com.example.greenapp.models.Post
import com.example.greenapp.models.Tip
import com.example.greenapp.models.User


@Database(entities = [Post::class, Tip::class, MyTip::class, User::class, OtherUser::class, Goal::class], version = 13)
@TypeConverters(Converters::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun tipsDao(): TipsDao
    abstract fun myTipsDao(): MyTipsDao
    abstract fun otherUsersDao(): OtherUserDao
    abstract fun usersDao(): UserDao
    abstract fun goalsDao(): GoalsDao

}

object AppLocalDatabase {

    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "ecotracker.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}