package com.example.tunebox.data.repository

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunebox.data.models.UserComment
import kotlinx.coroutines.launch

class CommentViewModel(private val repository: CommentRepository) : ViewModel() {
    // ... outros métodos ...

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