package com.example.tunebox.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class CommentViewModelFactory(private val repository: CommentRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}