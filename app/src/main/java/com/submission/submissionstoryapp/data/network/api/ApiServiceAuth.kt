package com.submission.submissionstoryapp.data.network.api

import com.submission.submissionstoryapp.data.network.authentication.LoginResponse
import com.submission.submissionstoryapp.data.network.authentication.SignupResponse
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
