package com.submission.submissionstoryapp.api

import com.submission.submissionstoryapp.data.model.LoginResponse
import com.submission.submissionstoryapp.data.model.SignupResponse
import retrofit2.http.*

interface ApiServiceAuth {

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


}
