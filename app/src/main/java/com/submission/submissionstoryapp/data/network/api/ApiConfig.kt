package com.submission.submissionstoryapp.data.network.api

import android.util.Log
import com.submission.submissionstoryapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    fun getAuthService(): ApiServiceAuth {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiServiceAuth::class.java)
    }


    fun getStoryService(userRepository: UserRepository): ApiServiceStory {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = Interceptor { chain ->
            val token = runBlocking {
                userRepository.getSession().first().token
            }

            if (token.isEmpty()) {
                throw IllegalStateException("Token cannot be empty")
            }

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            Log.d("AuthInterceptor", "Authorization header: ${request.headers}")
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiServiceStory::class.java)
    }

}
