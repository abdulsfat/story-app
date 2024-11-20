package com.submission.submissionstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.submissionstoryapp.data.model.ListStoryItem
import com.submission.submissionstoryapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableStateFlow<List<ListStoryItem>>(emptyList())
    val stories: StateFlow<List<ListStoryItem>> = _stories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStories()
                if (response.error == false) {
                    _stories.value = response.listStory?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Terjadi kesalahan."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengambil data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
