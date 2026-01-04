package com.example.tunebox.navigation

import android.icu.text.CaseMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.screens.*
import retrofit2.http.Url

@Composable
fun NavigationHost(
    currentRoute: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    accessToken: String,
    onLogout: () -> Unit,
    spotifyRepository: SpotifyRepository,
    albumTitle: String,
    artistName: String,
    coverUrl: String,
    onAlbumClick: (String, String, String) -> Unit
) {
    when (currentRoute) {

        "home" -> HomeContent(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken,
            onAlbumClick = onAlbumClick
        )

        "profile" -> {
            val profileViewModel = remember {
                ProfileViewModel(
                    repository = spotifyRepository,
                    accessToken = accessToken
                )
            }

            ProfileScreen(
                onBack = onLogout,
                viewModel = profileViewModel,
                onAlbumClick = onAlbumClick
            )
        }
        "comment" -> CommentScreen(
            albumTitle = albumTitle,
            artistName = artistName,
            coverUrl = coverUrl,
            onBack = onLogout,
            onSave = { comment, rating ->
                println("COMMENT: $comment | rating: $rating")
            }
        )

        else -> HomeContent(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken,
            onAlbumClick = onAlbumClick
        )
    }
}