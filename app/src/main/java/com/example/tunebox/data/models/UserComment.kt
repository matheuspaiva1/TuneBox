package com.example.tunebox.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_comments")
data class UserComment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: String,
    val albumTitle: String,
    val artistName: String,
    val coverUrl: String,
    val text: String,
    val rating: Int
)