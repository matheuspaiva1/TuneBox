package com.example.tunebox.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tunebox.data.repository.LikesViewModel
import com.example.tunebox.notifications.NotificationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikesScreen(
    viewModel: LikesViewModel,
    onItemClick: (String, String, String) -> Unit,
    notificationManager: NotificationManager // 1. Adicionado o parâmetro
) {
    val likes = viewModel.likesForUser().collectAsState(initial = emptyList())

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Curtidas",
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp
            )

            if (likes.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Você não curtiu nenhum item ainda.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    items(likes.value) { item ->
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(110.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            onClick = {
                                onItemClick(
                                    item.title,
                                    item.subtitle ?: "",
                                    item.imageUrl ?: ""
                                )
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(70.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Artista(s): ${item.subtitle ?: ""}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.toggleLike(
                                            item.itemId,
                                            item.title,
                                            item.subtitle,
                                            item.imageUrl,
                                            item.type
                                        )
                                        notificationManager.showLocalNotification(
                                            title = "Favoritos Atualizados",
                                            message = "Você removeu '${item.title}' da sua lista."
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Remover favorito",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}