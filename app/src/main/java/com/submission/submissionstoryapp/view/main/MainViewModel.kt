package com.submission.submissionstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.submission.submissionstoryapp.data.model.UserModel
import com.submission.submissionstoryapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
