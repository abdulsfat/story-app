package com.submission.submissionstoryapp.data.network.api

import com.submission.submissionstoryapp.data.network.story.DetailStoryResponse
import com.submission.submissionstoryapp.data.network.story.StoryResponse
import com.submission.submissionstoryapp.data.network.story.UploadStoryResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceStory {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

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

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @retrofit2.http.Query("location") location: Int = 1
    ): StoryResponse
}