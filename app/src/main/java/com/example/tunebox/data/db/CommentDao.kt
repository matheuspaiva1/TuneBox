package com.example.tunebox.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tunebox.data.models.UserComment
import kotlinx.coroutines.flow.Flow
@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: UserComment)

    @Query("SELECT * FROM user_comments WHERE userId = :userId ORDER BY id DESC")
    fun getCommentsForUser(userId: String): Flow<List<UserComment>>

    @Query(
        """
        SELECT 
            albumTitle AS albumTitle,
            artistName AS artistName,
            coverUrl AS coverUrl,
            COUNT(*) AS count
        FROM user_comments
        WHERE userId = :userId
        GROUP BY albumTitle, artistName, coverUrl
        ORDER BY count DESC
        LIMIT :limit
        """
    )
    fun getMostCommentedAlbums(
        userId: String,
        limit: Int = 10
    ): Flow<List<AlbumCommentCount>>
}