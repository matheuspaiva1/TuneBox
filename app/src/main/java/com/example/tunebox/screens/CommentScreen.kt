package com.example.tunebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tunebox.notifications.NotificationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    albumTitle: String,
    artistName: String,
    coverUrl: String,
    onBack: () -> Unit,
    onSave: (String, Int) -> Unit,
    existingRating: Int? = null,
    existingComment: String? = null,
    notificationManager: NotificationManager? = null
) {
    var rating by remember { mutableStateOf(existingRating ?: 0) }
    var comment by remember { mutableStateOf(existingComment ?: "") }

    Scaffold(
        topBar = {  }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommentHeaderCard(
                albumTitle = albumTitle,
                artistName = artistName,
                coverUrl = coverUrl,
                rating = rating,
                onRatingChange = { rating = it }
            )

            CommentInput(
                text = comment,
                onTextChange = { comment = it }
            )

            Button(
                onClick = {
                    onSave(comment, rating)
                    notificationManager?.showLocalNotification(
                        "Novo Comentário",
                        "Você avaliou o álbum $albumTitle com $rating estrelas."
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Salvar",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}


@Composable
fun CommentHeaderCard(
    albumTitle: String,
    artistName: String,
    coverUrl: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = albumTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            AsyncImage(
                model = coverUrl,
                contentDescription = albumTitle,
                modifier = Modifier
                    .size(200.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = artistName,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            RatingRow(
                rating = rating,
                onRatingChange = onRatingChange
            )
        }
    }
}

@Composable
fun RatingRow(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { index ->
            val filled = index < rating
            IconButton(onClick = { onRatingChange(index + 1) }) {
                Icon(
                    imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CommentInput(
    text: String,
    onTextChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Faça aqui seus comentários:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = {
                Text(
                    "Digite aqui seu comentário sobre o álbum ou música",
                    fontSize = 13.sp
                )
            },
            maxLines = 6
        )
    }
}