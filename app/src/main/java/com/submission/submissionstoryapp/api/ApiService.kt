package com.submission.submissionstoryapp.api

import com.submission.submissionstoryapp.data.model.LoginResponse
import com.submission.submissionstoryapp.data.model.SignupResponse
import com.submission.submissionstoryapp.data.model.StoryResponse
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse
}
