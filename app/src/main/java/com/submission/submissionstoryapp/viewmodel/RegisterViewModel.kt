package com.submission.submissionstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.data.model.SignupResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    var onRegisterSuccess: ((SignupResponse) -> Unit)? = null
    var onRegisterError: ((String) -> Unit)? = null

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.register(name, email, password)
                if (!response.error!!) {
                    onRegisterSuccess?.invoke(response)
                } else {
                    onRegisterError?.invoke(response.message ?: "Terjadi kesalahan.")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Gson().fromJson(errorBody, SignupResponse::class.java)
                    if (errorResponse.message?.contains("Email is already taken", true) == true) {
                        onRegisterError?.invoke("Email sudah terdaftar. Silakan gunakan email lain.")
                    } else {
                        onRegisterError?.invoke(errorResponse.message ?: "Terjadi kesalahan.")
                    }
                } else {
                    onRegisterError?.invoke("Terjadi kesalahan saat koneksi ke server.")
                }
            } catch (e: Exception) {
                onRegisterError?.invoke("Terjadi kesalahan: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

