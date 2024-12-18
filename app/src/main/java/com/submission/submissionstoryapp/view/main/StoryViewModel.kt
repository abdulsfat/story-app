package com.submission.submissionstoryapp.view.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class StoryViewModel(
    application: Application,
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)


    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val token = getTokenFromDataStore()

                if (token.isNotEmpty()) {
                    storyRepository.getStories()

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

    fun fetchStoriesWithLocation(): Flow<List<ListStoryItem>> {
        _isLoading.value = true
        return flow {
            try {
                val response = storyRepository.getStoriesWithLocation()
                emit(
                    response.listStory?.filterNotNull()?.filter { storyItem ->
                        storyItem.lat != null && storyItem.lon != null
                    } ?: emptyList()
                )
            } catch (e: Exception) {
                emit(emptyList())
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