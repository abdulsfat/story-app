package com.submission.submissionstoryapp.api

import com.submission.submissionstoryapp.data.model.DetailStoryResponse
import com.submission.submissionstoryapp.data.model.StoryResponse
import com.submission.submissionstoryapp.data.model.UploadStoryResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiServiceStory {
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