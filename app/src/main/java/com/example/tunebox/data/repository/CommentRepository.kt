package com.example.tunebox.data.repository

import com.example.tunebox.data.db.CommentDao
import com.example.tunebox.data.db.AlbumCommentCount
import com.example.tunebox.data.models.UserComment
import kotlinx.coroutines.flow.Flow

class CommentRepository(
    private val dao: CommentDao
) {
    fun getCommentsForUser(userId: String): Flow<List<UserComment>> =
        dao.getCommentsForUser(userId)

    suspend fun addComment(comment: UserComment) {
        dao.insertComment(comment)
    }
    fun getMostCommentedAlbums(
        userId: String,
        limit: Int = 10
    ): Flow<List<AlbumCommentCount>> =
        dao.getMostCommentedAlbums(userId, limit)
}
