package com.submission.submissionstoryapp.di

import android.content.Context
import com.submission.submissionstoryapp.api.ApiConfig
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.utils.UserPreference
import com.submission.submissionstoryapp.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)

        val token = runBlocking {
            pref.getSession().first().token
        }

        val apiServiceAuth = ApiConfig.getAuthService()
        val userRepository = UserRepository.getInstance(pref, apiServiceAuth)

        val apiService = ApiConfig.getStoryService(userRepository, token)

        return StoryRepository.getInstance(apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiServiceAuth = ApiConfig.getAuthService()
        return UserRepository.getInstance(userPreference, apiServiceAuth)
    }
}
