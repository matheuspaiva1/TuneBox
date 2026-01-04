package com.example.tunebox.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunebox.data.models.SpotifyUser
import com.example.tunebox.data.repository.SpotifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.tunebox.data.models.TrackItem
import com.example.tunebox.data.models.PlaylistItem

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: SpotifyUser? = null,
    val favoriteTracks: List<TrackItem> = emptyList(),
    val userPlaylists: List<PlaylistItem> = emptyList(),
    val error: String? = null
)

class ProfileViewModel(
    private val repository: SpotifyRepository,
    private val accessToken: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val user = repository.getCurrentUser(accessToken)
                val topTracks = repository.getUserTopTracksShort(accessToken, limit = 5)
                val playlists = repository.getUserPlaylistsShort(accessToken, limit = 5)

                println("PROFILE VM USER: $user")

                _uiState.value = ProfileUiState(
                    isLoading = false,
                    user = user,
                    favoriteTracks = topTracks,
                    userPlaylists = playlists
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

