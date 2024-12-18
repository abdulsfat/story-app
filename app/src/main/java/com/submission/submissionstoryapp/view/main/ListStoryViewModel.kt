package com.submission.submissionstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.data.repository.StoryRepository

class ListStoryViewModel(repository: StoryRepository) : ViewModel() {

    val listStory: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope).asLiveData()

}