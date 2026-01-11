package com.example.tunebox.data.repository

import com.example.tunebox.data.db.LikesDao
import com.example.tunebox.data.models.LikedItem
import kotlinx.coroutines.flow.Flow

class LikeRepository(
    private val dao: LikesDao
) {
    fun getLikesForUser(userId: String): Flow<List<LikedItem>> = dao.getLikesForUser(userId)

    suspend fun addLike(likedItem: LikedItem) {
        dao.insertLike(likedItem)
    }

    suspend fun removeLike(itemId: String, userId: String, type: String) {
        dao.deleteLike(itemId, userId, type)
    }

    fun getLikeCount(userId: String, itemId: String, type: String) =
        dao.getLikeCount(itemId, userId, type)
}

