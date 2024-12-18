package com.submission.submissionstoryapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.submission.submissionstoryapp.data.database.RemoteKeysEntity
import com.submission.submissionstoryapp.data.database.StoryDatabase
import com.submission.submissionstoryapp.data.database.StoryEntity
import com.submission.submissionstoryapp.data.network.api.ApiServiceStory
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.data.network.story.StoryResponse

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiServiceStory
) : RemoteMediator<Int, StoryEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryRemoteMediator"
    }

    override suspend fun initialize(): InitializeAction {
        Log.d(TAG, "initialize() called")
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            Log.d(TAG, "Fetching data from API: page = $page, pageSize = ${state.config.pageSize}")
            val response = apiService.getStories(page, state.config.pageSize)
            val storyEntities = response.toStoryEntityList()

            val endOfPaginationReached = storyEntities.isEmpty()
            Log.d(TAG, "API Response Success: Fetched ${storyEntities.size} stories")

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "Deleting all stories from database")
                    database.storyDao().deleteAllStories()
                }
                Log.d(TAG, "Inserting ${storyEntities.size} stories into database")
                database.storyDao().insertStories(storyEntities)
            }

            Log.d(TAG, "Returning MediatorResult.Success: endOfPaginationReached = $endOfPaginationReached")
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching data from API or inserting into database", e)
            return MediatorResult.Error(e)
        }
    }

    private fun ListStoryItem.toStoryEntity(): StoryEntity {
        return StoryEntity(
            id = this.id ?: "",
            name = this.name ?: "No Name",
            description = this.description ?: "No Description",
            photoUrl = this.photoUrl ?: "",
            createdAt = this.createdAt ?: "",
            lon = this.lon?.toString()?.toDoubleOrNull(),
            lat = this.lat?.toString()?.toDoubleOrNull()
        ).also {
            Log.d(TAG, "Mapping ListStoryItem to StoryEntity: id = ${it.id}")
        }
    }

    private fun StoryResponse.toStoryEntityList(): List<StoryEntity> {
        return this.listStory?.filterNotNull()?.map { it.toStoryEntity() } ?: emptyList()
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}
