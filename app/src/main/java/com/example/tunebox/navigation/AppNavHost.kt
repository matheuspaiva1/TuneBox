package com.example.tunebox.navigation
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tunebox.data.db.AppDatabase
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.LikeRepository
import com.example.tunebox.data.repository.LikesViewModel
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.notifications.NotificationManager
import com.example.tunebox.screens.*
import com.example.tunebox.ui.navigation.Screen
import com.example.tunebox.screens.CommentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tunebox.screens.CommentViewModelFactory
import com.example.tunebox.data.repository.LikesViewModelFactory
import com.example.tunebox.screens.SearchViewModelFactory


@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    accessToken: String,
    onLogout: () -> Unit,
    spotifyRepository: SpotifyRepository,
    currentUserId: String,
    comments: List<UserComment>,
    onAddComment: (UserComment) -> Unit,
    commentRepository: CommentRepository,
    profileViewModel: ProfileViewModel,
    appDatabase: AppDatabase,
    notificationManager: NotificationManager
) {

    val likesViewModel: LikesViewModel = viewModel(
        factory = LikesViewModelFactory(appDatabase.likesDao(), currentUserId)
    )

    val likeRepository = remember { LikeRepository(appDatabase.likesDao()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeContent(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                accessToken = accessToken,
                onAlbumClick = { title, artist, cover ->
                    navController.navigate(Screen.Comment.createRoute(Uri.encode(title), Uri.encode(artist), Uri.encode(cover)))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = onLogout,
                viewModel = profileViewModel,
                onAlbumClick = { title, artist, cover ->
                    navController.navigate(Screen.Comment.createRoute(Uri.encode(title), Uri.encode(artist), Uri.encode(cover)))
                }
            )
        }

        composable(Screen.Comments.route) {
            val commentViewModel: CommentViewModel = viewModel(
                factory = CommentViewModelFactory(commentRepository)
            )

            CommentListScreen(
                comments = comments,
                viewModel = commentViewModel,
                notificationManager = notificationManager
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModelFactory(spotifyRepository, accessToken)
            )

            val results by searchViewModel.results.collectAsState()
            val likedItems by likesViewModel.likesForUser().collectAsState(initial = emptyList())
            val favoriteIds = likedItems.map { it.itemId }.toSet()

            SearchScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                results = results,
                onQueryChange = { searchViewModel.onQueryChange(it) },
                onResultClick = { result ->
                    navController.navigate(Screen.Comment.createRoute(Uri.encode(result.title), Uri.encode(result.subtitle), Uri.encode(result.imageUrl)))
                },
                onFavoriteClick = { result ->
                    likesViewModel.toggleLike(
                        result.id, result.title, result.subtitle,
                        result.imageUrl, result.type ?: "track"
                    )
                },
                onCommentClick = { result ->
                    navController.navigate(Screen.Comment.createRoute(Uri.encode(result.title), Uri.encode(result.subtitle), Uri.encode(result.imageUrl)))
                },
                favoriteIds = favoriteIds,
                notificationManager = notificationManager
            )
        }

        composable(Screen.Favorites.route) {
            LikesScreen(
                viewModel = likesViewModel,
                onItemClick = { title, artist, cover ->
                    navController.navigate(Screen.Comment.createRoute(Uri.encode(title), Uri.encode(artist), Uri.encode(cover)))
                },
                notificationManager = notificationManager
            )
        }

        composable(
            Screen.Comment.route,
            arguments = listOf(
                navArgument("albumTitle") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "" },
                navArgument("artistName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "" },
                navArgument("coverUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val albumTitle = backStackEntry.arguments?.getString("albumTitle")?.let { Uri.decode(it) } ?: ""
            val artistName = backStackEntry.arguments?.getString("artistName")?.let { Uri.decode(it) } ?: ""
            val coverUrl = backStackEntry.arguments?.getString("coverUrl")?.let { Uri.decode(it) } ?: ""

            CommentScreen(
                albumTitle = albumTitle,
                artistName = artistName,
                coverUrl = coverUrl,
                onBack = { navController.popBackStack() },
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
                    navController.popBackStack()
                },
                notificationManager = notificationManager
            )
        }
    }
}