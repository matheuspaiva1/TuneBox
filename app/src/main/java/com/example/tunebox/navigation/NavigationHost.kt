package com.example.tunebox.navigation

import androidx.compose.runtime.Composable
import com.example.tunebox.screens.*

@Composable
fun NavigationHost(
    currentRoute: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    accessToken: String,
    onLogout: () -> Unit
) {
    when (currentRoute) {
        "home" -> HomeContent(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken
        )

        /**
        "search" -> SearchScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken
        )

        "add" -> AddScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme
        )

        "playing" -> NowPlayingScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme
        )

        "profile" -> ProfileScreen(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            onLogout = onLogout
        ) **/

        else -> HomeContent(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken
        )
    }
}
