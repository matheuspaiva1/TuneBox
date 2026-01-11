package com.example.tunebox.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tunebox.data.models.LikedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LikesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikedItem)

    @Query("DELETE FROM liked_items WHERE itemId = :itemId AND userId = :userId AND type = :type")
    suspend fun deleteLike(itemId: String, userId: String, type: String)

    @Query("SELECT * FROM liked_items WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLikesForUser(userId: String): Flow<List<LikedItem>>

    @Query("SELECT * FROM liked_items WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getLikesForUserByType(userId: String, type: String): Flow<List<LikedItem>>

    @Query("SELECT COUNT(*) FROM liked_items WHERE itemId = :itemId AND userId = :userId AND type = :type")
    fun getLikeCount(itemId: String, userId: String, type: String): Flow<Int>
}

