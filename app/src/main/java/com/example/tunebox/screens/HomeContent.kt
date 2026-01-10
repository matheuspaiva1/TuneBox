package com.example.tunebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tunebox.data.models.SpotifyAlbum
import com.example.tunebox.data.models.SpotifyTrack
import com.example.tunebox.data.repository.SpotifyRepository
import kotlinx.coroutines.launch

@Composable
fun HomeContent(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    accessToken: String,
    onAlbumClick: (title: String, artist: String, cover: String, artistImageUrl: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var albums by remember { mutableStateOf<List<SpotifyAlbum>>(emptyList()) }
    var tracks by remember { mutableStateOf<List<SpotifyTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val repository = SpotifyRepository()

    LaunchedEffect(accessToken) {
        scope.launch {
            albums = repository.getMostListenedAlbums(accessToken)
            tracks = repository.getTopTracks(accessToken)
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Abas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabButton(
                text = "Álbuns",
                isSelected = selectedTab == 0,
                onClick = { selectedTab = 0 }
            )
            TabButton(
                text = "Músicas",
                isSelected = selectedTab == 1,
                onClick = { selectedTab = 1 }
            )
        }

        Text(
            text = "Mais Escutados",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            when (selectedTab) {
                0 -> {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(albums) { album ->
                            AlbumCard(
                                album = album,
                                onClick = {
                                    val title = album.name
                                    val artist = album.artists.firstOrNull()?.name ?: ""
                                    val cover = album.images.firstOrNull()?.url ?: ""
                                    val artistImage = album.artists.firstOrNull()?.images?.firstOrNull()?.url ?: ""

                                    onAlbumClick(title, artist, cover, artistImage)
                                }
                            )
                        }
                    }
                }
                1 -> {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(tracks) { track ->
                            TrackCard(track)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AlbumCard(
    album: SpotifyAlbum,
    onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable{ onClick()}
    ) {
        if (album.images.isNotEmpty()) {
            AsyncImage(
                model = album.images[0].url,
                contentDescription = album.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = album.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun TrackCard(track: SpotifyTrack) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (track.album.images.isNotEmpty()) {
            AsyncImage(
                model = track.album.images[0].url,
                contentDescription = track.name,
                modifier = Modifier
                    .size(60.dp),
                contentScale = ContentScale.Crop
            )
        }


        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = track.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = track.artists.joinToString(", ") { it.name },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${track.album.name}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        Text(
            text = formatMillisToMinutes(track.duration_ms),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

fun formatMillisToMinutes(millis: Int): String {
    val minutes = millis / 1000 / 60
    val seconds = (millis / 1000) % 60
    return String.format("%d:%02d", minutes, seconds)
}
