package com.submission.submissionstoryapp.data.repository

import com.submission.submissionstoryapp.api.ApiService
import com.submission.submissionstoryapp.data.model.StoryResponse
import com.submission.submissionstoryapp.utils.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryRepository(private val apiService: ApiService) {

    suspend fun getStories(): StoryResponse {
        return withContext(Dispatchers.IO) {
            apiService.getStories()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = StoryRepository(apiService)
                INSTANCE = instance
                instance
            }
        }
    }

}
