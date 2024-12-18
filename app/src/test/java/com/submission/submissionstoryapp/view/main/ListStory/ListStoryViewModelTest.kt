package com.submission.submissionstoryapp.view.main.ListStory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.view.adapter.StoryAdapter
import com.submission.submissionstoryapp.view.main.DataDummy
import com.submission.submissionstoryapp.view.main.ListStoryViewModel
import com.submission.submissionstoryapp.view.main.MainDispatcherRule
import com.submission.submissionstoryapp.view.main.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ListStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private val storyRepository: StoryRepository = mockk()

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyListStoryItem()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)

        coEvery { storyRepository.getStories() } returns flowOf(data)


        val listStoryViewModel = ListStoryViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = listStoryViewModel.listStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())

        coEvery { storyRepository.getStories() } returns flowOf(data)

        val listStoryViewModel = ListStoryViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = listStoryViewModel.listStory.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : androidx.paging.PagingSource<Int, ListStoryItem>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<Int, ListStoryItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}