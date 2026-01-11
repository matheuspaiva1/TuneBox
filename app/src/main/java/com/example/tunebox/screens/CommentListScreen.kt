package com.example.tunebox.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.repository.CommentViewModel
import com.example.tunebox.notifications.NotificationManager

@Composable
fun CommentListScreen(
    comments: List<UserComment>,
    viewModel: CommentViewModel, // Recebe a ViewModel
    notificationManager: NotificationManager // Adicione este parâmetro
    ) {
    var editingComment by remember { mutableStateOf<UserComment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Seus comentários",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (comments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Você ainda não comentou nada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(comments) { comment ->
                    CommentListItem(
                        comment = comment,
                        onEdit = {
                            editingComment = comment
                        },
                        onDelete = { viewModel.deleteComment(comment) },
                        notificationManager = notificationManager
                    )
                }
            }
        }
    }

    // Se houver um comentário sendo editado, mostra o Dialog
    editingComment?.let { comment ->
        EditCommentDialog(
            comment = comment,
            onDismiss = { editingComment = null },
            onConfirm = { updatedComment ->
                viewModel.updateComment(updatedComment)
                editingComment = null
                notificationManager.showLocalNotification(
                    "Comentário Atualizado",
                    "Suas alterações no álbum ${updatedComment.albumTitle} foram salvas."
                )
            }
        )
    }
}

@Composable
fun EditCommentDialog(
    comment: UserComment,
    onDismiss: () -> Unit,
    onConfirm: (UserComment) -> Unit
) {
    // Estados locais para edição antes de salvar
    var text by remember { mutableStateOf(comment.text) }
    var rating by remember { mutableIntStateOf(comment.rating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Avaliação") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(comment.albumTitle, style = MaterialTheme.typography.titleMedium)

                // Seletor de Estrelas Interativo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val starValue = index + 1
                        IconButton(onClick = { rating = starValue }) {
                            Icon(
                                imageVector = if (starValue <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Seu comentário") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Cria uma cópia do comentário com os novos dados
                    val updated = comment.copy(text = text, rating = rating)
                    onConfirm(updated)
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CommentListItem(
    comment: UserComment,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    notificationManager: NotificationManager // Adicione este parâmetro

) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = comment.coverUrl,
                    contentDescription = comment.albumTitle,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f).padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = comment.albumTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = comment.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < comment.rating)
                                    Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = comment.text,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { showDeleteConfirmation = true }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Deletar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Deletar comentário?") },
            text = { Text("Tem certeza que deseja deletar este comentário?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                        notificationManager.showLocalNotification(
                            "Comentário Removido",
                            "O comentário do álbum ${comment.albumTitle} foi excluído."
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Deletar", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
