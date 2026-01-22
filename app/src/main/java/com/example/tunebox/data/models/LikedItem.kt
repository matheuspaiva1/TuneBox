package com.example.tunebox.data.models

import androidx.room.Entity

@Entity(
    tableName = "liked_items",
    primaryKeys = ["itemId", "userId", "type"]
)
data class LikedItem(
    val itemId: String,
    val userId: String,
    val type: String,
    val title: String,
    val subtitle: String?,
    val imageUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)