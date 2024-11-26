package com.submission.submissionstoryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.submission.submissionstoryapp.data.model.ListStoryItem
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoryViewModel(
    application: Application,
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _stories = MutableStateFlow<List<ListStoryItem>>(emptyList())
    val stories: StateFlow<List<ListStoryItem>> = _stories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val token = getTokenFromDataStore()

                if (token.isNotEmpty()) {
                    val response = storyRepository.getStories()


                    if (response.error == false) {
                        _stories.value = response.listStory?.filterNotNull() ?: emptyList()
                    } else {
                        _errorMessage.value = response.message ?: "Terjadi kesalahan."
                    }
                } else {
                    _errorMessage.value = "Token kosong. Harap login terlebih dahulu."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengambil data: ${e.localizedMessage ?: e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getTokenFromDataStore(): String {
        val user = userRepository.getSession().first()
        val token = user.token
        return token
    }

}