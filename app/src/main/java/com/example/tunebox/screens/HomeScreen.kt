package com.example.tunebox.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.tunebox.components.TuneBoxBottomNavigation
import com.example.tunebox.data.db.AppDatabase
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.navigation.AppNavHost
import com.example.tunebox.notifications.NotificationManager
import kotlinx.coroutines.launch

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
    val spotifyRepository = remember { SpotifyRepository() }

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

    val context = LocalContext.current
    val appDatabase = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "tunebox-db")
            .fallbackToDestructiveMigration().build()
    }
    val commentRepository = remember { CommentRepository(appDatabase.commentDao()) }
    val profileViewModel = remember {
        ProfileViewModel(spotifyRepository, accessToken, commentRepository, currentUserId!!)
    }
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
            spotifyRepository = spotifyRepository,
            currentUserId = currentUserId!!,
            comments = comments,
            onAddComment = { newComment ->
                scope.launch {
                    commentRepository.addComment(newComment)
                }
            },
            commentRepository = commentRepository,
            profileViewModel = profileViewModel,
            appDatabase = appDatabase,
            notificationManager = notificationManager
        )
    }
}