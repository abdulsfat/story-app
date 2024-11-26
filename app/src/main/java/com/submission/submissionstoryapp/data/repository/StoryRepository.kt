package com.submission.submissionstoryapp.data.repository

import com.submission.submissionstoryapp.api.ApiServiceStory
import com.submission.submissionstoryapp.data.model.StoryResponse


class StoryRepository private constructor(private val apiServiceStory: ApiServiceStory) {

    suspend fun getStories(): StoryResponse {
        return apiServiceStory.getStories()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiServiceStory: ApiServiceStory): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiServiceStory).also { instance = it }
            }
        }
    }
}

