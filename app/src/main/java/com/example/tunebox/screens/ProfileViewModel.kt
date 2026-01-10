package com.example.tunebox.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunebox.data.db.AlbumCommentCount
import com.example.tunebox.data.models.SpotifyUser
import com.example.tunebox.data.models.TrackItem
import com.example.tunebox.data.models.PlaylistItem
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.SpotifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUserUi(
    val id: String,
    val displayName: String,
    val imageUrl: String?,
    val customName: String? = null
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: ProfileUserUi? = null,
    val favoriteTracks: List<TrackItem> = emptyList(),
    val userPlaylists: List<PlaylistItem> = emptyList(),
    val mostCommentedAlbums: List<AlbumCommentCount> = emptyList(),
    val error: String? = null
)

class ProfileViewModel(
    private val repository: SpotifyRepository,
    private val accessToken: String,
    private val commentRepository: CommentRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUser()
        observeMostCommented()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val user: SpotifyUser? = repository.getCurrentUser(accessToken)
                val topTracks = repository.getUserTopTracksShort(accessToken, limit = 5)
                val playlists = repository.getUserPlaylistsShort(accessToken, limit = 5)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = user?.let {
                        ProfileUserUi(
                            id = it.id,
                            displayName = it.displayName ?: "User",
                            imageUrl = it.imageUrl,
                            customName = _uiState.value.user?.customName
                        )
                    },
                    favoriteTracks = topTracks,
                    userPlaylists = playlists,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    private fun observeMostCommented() {
        viewModelScope.launch {
            commentRepository
                .getMostCommentedAlbums(userId = userId, limit = 10)
                .collect { albums ->
                    _uiState.value = _uiState.value.copy(
                        mostCommentedAlbums = albums
                    )
                }
        }
    }
    fun updateDisplayName(newName: String) {
        _uiState.value = _uiState.value.copy(
            user = _uiState.value.user?.copy(
                customName = newName
            )
        )
    }
}