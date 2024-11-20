package com.submission.submissionstoryapp.api

import com.submission.submissionstoryapp.data.model.DetailStoryResponse
import com.submission.submissionstoryapp.data.model.LoginResponse
import com.submission.submissionstoryapp.data.model.SignupResponse
import com.submission.submissionstoryapp.data.model.StoryResponse
import com.submission.submissionstoryapp.data.model.UploadStoryResponse
import okhttp3.MultipartBody
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

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") storyId: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: okhttp3.RequestBody,
        @Part("lat") lat: okhttp3.RequestBody?,
        @Part("lon") lon: okhttp3.RequestBody?
    ): UploadStoryResponse
}
