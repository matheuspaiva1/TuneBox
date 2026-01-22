package com.example.tunebox.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tunebox.R
import com.example.tunebox.ui.navigation.Screen

@Composable
fun TuneBoxBottomNavigation(navController: NavController) {
    val currentDestination by navController.currentBackStackEntryAsState()
    val screens = listOf(
        Screen.Home, Screen.Search, Screen.Favorites,
        Screen.Comments, Screen.Profile
    )

    NavigationBar ( containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ){
        screens.forEach { screen ->
            val selected = currentDestination?.destination?.hierarchy
                ?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Home -> Icons.Default.Home
                            Screen.Search -> Icons.Default.Search
                            Screen.Favorites -> Icons.Default.Favorite
                            Screen.Comments -> Icons.Default.Comment
                            Screen.Profile -> Icons.Default.Person
                            else -> Icons.Default.Home
                        },
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        when (screen) {
                            Screen.Home -> "Home"
                            Screen.Search -> "Search"
                            Screen.Favorites -> "Favorites"
                            Screen.Comments -> "Comments"
                            Screen.Profile -> "Profile"
                            else -> "Home"
                        }
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}