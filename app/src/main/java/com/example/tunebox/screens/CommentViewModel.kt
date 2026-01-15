package com.example.tunebox.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunebox.data.models.UserComment
import com.example.tunebox.data.repository.CommentRepository
import kotlinx.coroutines.launch

class CommentViewModel(private val repository: CommentRepository) : ViewModel() {
    fun updateComment(comment: UserComment) {
        viewModelScope.launch {
            repository.updateComment(comment)
        }
    }

    fun deleteComment(comment: UserComment) {
        viewModelScope.launch {
            repository.deleteComment(comment)
        }
    }
}