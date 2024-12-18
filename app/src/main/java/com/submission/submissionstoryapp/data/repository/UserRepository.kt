package com.submission.submissionstoryapp.data.repository

import android.util.Log
import com.submission.submissionstoryapp.data.network.api.ApiServiceAuth
import com.submission.submissionstoryapp.data.network.authentication.LoginResponse
import com.submission.submissionstoryapp.data.network.authentication.SignupResponse
import com.submission.submissionstoryapp.data.model.UserModel
import com.submission.submissionstoryapp.utils.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiServiceAuth
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
        Log.d("UserPreference", "Token saved: ${user.token}")
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): SignupResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiServiceAuth
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }

    }
}
