package com.example.tunebox.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Deprecated("Use LikesScreen with LikesViewModel instead")
@Composable
fun FavoritesScreen(onItemClick: (String, String, String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Favoritos migrados para Curtidas (Likes).", color = MaterialTheme.colorScheme.onBackground)
    }
}
