package com.example.tunebox.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunebox.data.models.LikedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LikesViewModel(private val repository: LikeRepository, private val userId: String) : ViewModel() {

    fun likesForUser(): Flow<List<LikedItem>> = repository.getLikesForUser(userId)

    fun isLikedFlow(itemId: String, type: String): Flow<Boolean> =
        repository.getLikeCount(userId, itemId, type).map { it > 0 }

    fun toggleLike(itemId: String, title: String, subtitle: String?, imageUrl: String?, type: String) {
        viewModelScope.launch {
            val count = repository.getLikeCount(userId, itemId, type).first()
            if (count > 0) {
                repository.removeLike(itemId, userId, type)
            } else {
                val liked = LikedItem(
                    itemId = itemId,
                    userId = userId,
                    type = type,
                    title = title,
                    subtitle = subtitle,
                    imageUrl = imageUrl
                )
                repository.addLike(liked)
            }
        }
    }
}