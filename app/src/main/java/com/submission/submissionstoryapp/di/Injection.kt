package com.submission.submissionstoryapp.di

import android.content.Context
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.utils.UserPreference
import com.submission.submissionstoryapp.utils.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}