package com.example.tunebox.navigation

import android.icu.text.CaseMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.CommentViewModel
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.screens.*
import retrofit2.http.Url
import com.example.tunebox.data.repository.LikeRepository
import com.example.tunebox.data.repository.LikesViewModel
import com.example.tunebox.data.db.AppDatabase

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
    onAlbumClick: (String, String, String) -> Unit,
    currentUserId: String,
    comments: List<UserComment>,
    onAddComment: (UserComment) -> Unit,
    commentRepository: CommentRepository,
    profileViewModel: ProfileViewModel,
    appDatabase: AppDatabase
) {
    val likeRepository = remember { LikeRepository(appDatabase.likesDao()) }
    val likesViewModel = remember(currentUserId) { LikesViewModel(likeRepository, currentUserId) }

    when (currentRoute) {

        "home" -> HomeContent(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken,
            onAlbumClick = onAlbumClick
        )

        "profile" -> {
            ProfileScreen(
                onBack = onLogout,
                viewModel = profileViewModel,
                onAlbumClick = onAlbumClick
            )
        }

        "comments" -> CommentListScreen(
            comments = comments,
            viewModel = CommentViewModel(commentRepository)
        )

        "search" -> {
            val searchViewModel = remember {
                SearchViewModel(
                    repository = spotifyRepository,
                    accessToken = accessToken
                )
            }
            val results by searchViewModel.results.collectAsState()
            val likedItems by likesViewModel.likesForUser().collectAsState(initial = emptyList())
            val favoriteIds = likedItems.map { it.itemId }.toSet()

            SearchScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                results = results,
                onQueryChange = { searchViewModel.onQueryChange(it) },
                onResultClick = { result ->
                    onAlbumClick(result.title, result.subtitle, result.imageUrl)
                },
                onFavoriteClick = { result -> likesViewModel.toggleLike(result.id, result.title, result.subtitle, result.imageUrl, result.type) },
                onCommentClick = { result ->
                    onAlbumClick(result.title, result.subtitle, result.imageUrl)
                },
                favoriteIds = favoriteIds
            )
        }


        "favorites" -> {
            LikesScreen(
                viewModel = likesViewModel,
                onItemClick = { title, artist, cover ->
                    onAlbumClick(title, artist, cover)
                }
            )
        }

        "comment" -> CommentScreen(
            albumTitle = albumTitle,
            artistName = artistName,
            coverUrl = coverUrl,
            onBack = onLogout,
            onSave = { text, rating ->
                val comment = UserComment(
                    id = System.currentTimeMillis(),
                    userId = currentUserId,
                    albumTitle = albumTitle,
                    artistName = artistName,
                    coverUrl = coverUrl,
                    text = text,
                    rating = rating
                )
                onAddComment(comment)
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