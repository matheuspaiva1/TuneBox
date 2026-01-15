package com.example.tunebox.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tunebox.notifications.NotificationManager

data class SearchResultUi(
    val id: String,
    val title: String,      // nome da música/álbum
    val subtitle: String,   // artista
    val imageUrl: String,
    val artistImageUrl: String, // imagem do artista (nova)
    val type: String // "track" | "album" | "artist"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    results: List<SearchResultUi>,
    onQueryChange: (String) -> Unit,
    onResultClick: (SearchResultUi) -> Unit = {},
    onFavoriteClick: (SearchResultUi) -> Unit = {},
    onCommentClick: (SearchResultUi) -> Unit = {},
    favoriteIds: Set<String> = emptySet(),
    notificationManager: NotificationManager
) {
    var query by remember { mutableStateOf("") }

    Scaffold(

    ) { paddingValues ->
        val topPadding = maxOf(paddingValues.calculateTopPadding() - 28.dp, 0.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = topPadding, start = 16.dp, end = 16.dp, bottom = paddingValues.calculateBottomPadding())
        ) {
            // Barra de busca
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text("Pesquise por música, álbum e artistas")
                },
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Lista de resultados
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(results) { item ->
                    val isFavorite = favoriteIds.contains(item.id)

                    SearchResultCard(
                        result = item,
                        onClick = {
                            onResultClick(item)
                            val title = if (isFavorite) "Removido" else "Adicionado"
                            val message = if (isFavorite)
                                "'${item.title}' saiu dos seus favoritos."
                            else
                                "'${item.title}' foi adicionado aos seus favoritos!"

                            notificationManager.showLocalNotification(title, message)
                        },
                        onFavoriteClick = { onFavoriteClick(item) },
                        onCommentClick = { onCommentClick(item) },
                        isFavorite = favoriteIds.contains(item.id),
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    result: SearchResultUi,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCommentClick: () -> Unit,
    isFavorite: Boolean,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = result.imageUrl,
                contentDescription = result.title,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Artista(s): ${result.subtitle}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right-side: apenas o ícone de coração, posicionado no canto superior direito do card
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .height(70.dp)
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}