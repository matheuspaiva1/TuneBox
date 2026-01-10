package com.example.tunebox.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tunebox.data.models.UserComment

@Database(
    entities = [UserComment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao
}