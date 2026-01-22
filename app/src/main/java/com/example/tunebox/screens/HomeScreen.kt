package com.example.tunebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import com.example.tunebox.components.TuneBoxBottomNavigation
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.navigation.AppNavHost
import com.example.tunebox.notifications.NotificationManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    accessToken: String,
    onLogout: () -> Unit,
    notificationManager: NotificationManager
) {
    val navController = rememberNavController()
    var currentUserId by remember { mutableStateOf<String?>(null) }
    val spotifyRepository: SpotifyRepository = koinInject()

    LaunchedEffect(accessToken) {
        val user = spotifyRepository.getCurrentUser(accessToken)
        currentUserId = user?.id
    }

    if (currentUserId == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val commentRepository: CommentRepository = koinInject()
    val profileViewModel: ProfileViewModel = koinViewModel(
        parameters = { parametersOf(accessToken, currentUserId!!) }
    )
    val commentsFlow = remember(currentUserId) { commentRepository.getCommentsForUser(currentUserId!!) }
    val comments by commentsFlow.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TuneBox",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            TuneBoxBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            paddingValues = paddingValues,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            accessToken = accessToken,
            onLogout = onLogout,
            currentUserId = currentUserId!!,
            comments = comments,
            onAddComment = { newComment ->
                scope.launch {
                    commentRepository.addComment(newComment)
                }
            },
            profileViewModel = profileViewModel,
            notificationManager = notificationManager
        )
    }
}