package com.example.tunebox.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.models.LikedItem

@Database(
    entities = [UserComment::class, LikedItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao
    abstract fun likesDao(): LikesDao
}