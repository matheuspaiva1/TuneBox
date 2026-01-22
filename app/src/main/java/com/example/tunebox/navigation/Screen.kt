package com.example.tunebox.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Comments : Screen("comments")
    object Comment : Screen("comment/{albumTitle}/{artistName}/{coverUrl}") {
        fun createRoute(albumTitle: String, artistName: String, coverUrl: String) =
            "comment/$albumTitle/$artistName/$coverUrl"
    }
}