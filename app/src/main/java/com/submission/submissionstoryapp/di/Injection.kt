package com.submission.submissionstoryapp.di

import android.content.Context
import androidx.room.Room
import com.submission.submissionstoryapp.data.database.StoryDatabase
import com.submission.submissionstoryapp.data.network.api.ApiConfig
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.utils.UserPreference
import com.submission.submissionstoryapp.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    // Menambahkan pembuatan StoryDatabase di sini
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)

        // Dapatkan token user (biasanya digunakan untuk autentikasi)
        runBlocking {
            pref.getSession().first().token
        }

        // Inisialisasi apiService untuk Story
        val apiServiceAuth = ApiConfig.getAuthService()
        val userRepository = UserRepository.getInstance(pref, apiServiceAuth)

        // Dapatkan apiService untuk Story
        val apiService = ApiConfig.getStoryService(userRepository)

        // Membuat StoryDatabase (biasanya diinisialisasi dengan Room.databaseBuilder)
        val storyDatabase = Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "story_database" // Nama database, sesuaikan dengan nama yang digunakan
        ).build()

        // Mengembalikan instance StoryRepository dengan StoryDatabase dan ApiServiceStory
        return StoryRepository.getInstance(storyDatabase, apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiServiceAuth = ApiConfig.getAuthService()
        return UserRepository.getInstance(userPreference, apiServiceAuth)
    }
}
