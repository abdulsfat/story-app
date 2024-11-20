package com.submission.submissionstoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.submissionstoryapp.data.model.UserModel
import com.submission.submissionstoryapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<UserModel?>()
    val loginResult: LiveData<UserModel?> = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (!response.error) {
                    val user = UserModel(email, response.loginResult.token ?: "")
                    repository.saveSession(user)
                    _loginResult.postValue(user)
                } else {
                    _errorMessage.postValue(response.message)
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _errorMessage.postValue("401")
                } else {
                    _errorMessage.postValue("Gagal login: ${e.message}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}
