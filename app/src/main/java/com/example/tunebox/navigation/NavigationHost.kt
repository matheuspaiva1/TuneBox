package com.example.tunebox.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.screens.*

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
    artistImageUrl: String,
    onAlbumClick: (String, String, String, String) -> Unit
) {
    when (currentRoute) {

        "home" -> HomeContent(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken,
            onAlbumClick = onAlbumClick
        )

        "search" -> {
            val searchViewModel = remember {
                SearchViewModel(
                    repository = spotifyRepository,
                    accessToken = accessToken
                )
            }
            val results by searchViewModel.results.collectAsState()

            SearchScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                results = results,
                onQueryChange = { searchViewModel.onQueryChange(it) },
                onResultClick = { result ->
                    onAlbumClick(result.title, result.subtitle, result.imageUrl, result.artistImageUrl)
                },
                onFavoriteClick = { /* favoritar */ },
                onCommentClick = { result ->
                    onAlbumClick(result.title, result.subtitle, result.imageUrl, result.artistImageUrl)
                }
            )
        }

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
            artistImageUrl = artistImageUrl,
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