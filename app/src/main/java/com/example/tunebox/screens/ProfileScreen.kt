package com.example.tunebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ProfileViewModel,
    onAlbumClick: (String, String, String) -> Unit
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(state.user?.displayName ?: "") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding(),
                    bottom = 4.dp
                )
        ) {

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp)
                    .offset(y = (-70).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Box(
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit name") },
                                onClick = {
                                    showMenu = false
                                    editedName = state.user?.displayName ?: ""
                                    showEditNameDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Quit", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    onBack()
                                }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(140.dp)
                        ) {
                            AsyncImage(
                                model = state.user?.imageUrl
                                    ?: "https://picsum.photos/200",
                                contentDescription = "Profile photo",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = state.user?.customName ?: state.user?.displayName ?: "User",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Text(
                text = "Meus favoritos",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.offset(y = (-4).dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.favoriteTracks) { trackItem ->
                    FavoriteCard(
                        imageUrl = trackItem.album.images.firstOrNull()?.url ?: "",
                        title = trackItem.name,
                        onClick = {
                            onAlbumClick(
                                trackItem.name,
                                "",
                                trackItem.album.images.firstOrNull()?.url ?: ""
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Mais comentados",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))

            AlbumRow(
                urls = state.mostCommentedAlbums
                    .map { it.coverUrl }
                    .filter { it.isNotEmpty() }
            )
        }

        if (showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                title = { Text("Editar nome") },
                text = {
                    TextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateDisplayName(editedName)
                            showEditNameDialog = false
                        }
                    ) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun AlbumRow(urls: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(urls) { url ->
            Card(
                modifier = Modifier
                    .width(110.dp)
                    .height(140.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun FavoriteCard(
    imageUrl: String,
    title: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        Text(
            text = title,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}