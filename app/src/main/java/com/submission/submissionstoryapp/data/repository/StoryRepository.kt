package com.submission.submissionstoryapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.submission.submissionstoryapp.data.StoryRemoteMediator
import com.submission.submissionstoryapp.data.database.StoryDatabase
import com.submission.submissionstoryapp.data.network.api.ApiServiceStory
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.utils.toListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiServiceStory: ApiServiceStory
) {

    suspend fun getStoriesWithLocation() = apiServiceStory.getStoriesWithLocation(1)

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiServiceStory),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow.map { pagingData->
            pagingData.map { StoryEntity->
                StoryEntity.toListStoryItem()
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            storyDatabase: StoryDatabase,
            apiServiceStory: ApiServiceStory
        ): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiServiceStory).also { instance = it }
            }
        }
    }
}
