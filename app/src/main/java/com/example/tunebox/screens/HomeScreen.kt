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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.tunebox.components.TuneBoxBottomNavigation
import com.example.tunebox.data.db.AppDatabase
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.repository.CommentRepository
import com.example.tunebox.data.repository.SpotifyRepository
import com.example.tunebox.navigation.NavigationHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    accessToken: String,
    onLogout: () -> Unit
) {
    var currentRoute by remember { mutableStateOf("home") }

    var selectedAlbumTitle by remember { mutableStateOf("Rodeo") }
    var selectedArtistName by remember { mutableStateOf("Travis Scott") }
    var selectedCoverUrl by remember { mutableStateOf("https://picsum.photos/400") }

    val spotifyRepository = remember { SpotifyRepository() }
    var currentUserId by remember { mutableStateOf<String?>(null) }

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
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tunebox-db"
        ).fallbackToDestructiveMigration().build()    }

    val commentRepository = remember {
        CommentRepository(appDatabase.commentDao())
    }

    val profileViewModel = remember {
        ProfileViewModel(
            repository = spotifyRepository,
            accessToken = accessToken,
            commentRepository = commentRepository,
            userId = currentUserId!!
        )
    }

    val commentsFlow = remember(currentUserId) {
        commentRepository.getCommentsForUser(currentUserId!!)
    }
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
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
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
            TuneBoxBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { currentRoute = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            NavigationHost(
                currentRoute = currentRoute,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                accessToken = accessToken,
                onLogout = onLogout,
                spotifyRepository = spotifyRepository,
                albumTitle = selectedAlbumTitle,
                artistName = selectedArtistName,
                coverUrl = selectedCoverUrl,
                onAlbumClick = { title, artist, cover ->
                    selectedAlbumTitle = title
                    selectedArtistName = artist
                    selectedCoverUrl = cover
                    currentRoute = "comment"
                },
                currentUserId = currentUserId!!,
                comments = comments,
                onAddComment = { newComment ->
                    scope.launch {
                        commentRepository.addComment(newComment)
                    }
                    currentRoute = "comments"
                },
                commentRepository = commentRepository,
                profileViewModel = profileViewModel,
                appDatabase = appDatabase
            )
        }
    }
}