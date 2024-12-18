package com.submission.submissionstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.view.adapter.StoryAdapter

class ListStoryViewModel(repository: StoryRepository) : ViewModel() {

    val listStory: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope).asLiveData()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dialogMessage = MutableLiveData<String?>()
    val dialogMessage: LiveData<String?> = _dialogMessage

    private val _loadState = MutableLiveData<CombinedLoadStates>()
    val loadState: LiveData<CombinedLoadStates> = _loadState

    fun observeLoadState(loadStates: CombinedLoadStates) {
        _loadState.postValue(loadStates)

        val errorState = loadStates.source.append as? LoadState.Error
            ?: loadStates.source.prepend as? LoadState.Error
            ?: loadStates.append as? LoadState.Error
            ?: loadStates.prepend as? LoadState.Error
        errorState?.let {
            _dialogMessage.postValue(it.error.localizedMessage)
        }
    }

    fun clearDialogMessage() {
        _dialogMessage.value = null
    }

    fun refreshStories(adapter: StoryAdapter) {
        adapter.refresh()
    }
}