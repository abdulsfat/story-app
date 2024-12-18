package com.submission.submissionstoryapp.view.addstory

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.submission.submissionstoryapp.data.network.api.ApiConfig
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.utils.UserPreference
import com.submission.submissionstoryapp.utils.dataStore
import com.submission.submissionstoryapp.utils.reduceFileImage
import com.submission.submissionstoryapp.utils.uriToFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreference: UserPreference = UserPreference.getInstance(application.dataStore)
    private var currentLocation: Location? = null

    fun uploadStory(
        descriptionText: String,
        currentImageUri: Uri?,
        currentLocation: Location?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (descriptionText.isEmpty()) {
            onError("Description cannot be empty")
            return
        }

        if (currentImageUri == null) {
            onError("Image cannot be empty")
            return
        }

        currentImageUri.let { uri ->
            try {
                val imageFile = uriToFile(uri, getApplication()).reduceFileImage()

                val latRequestBody: okhttp3.RequestBody? = currentLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
                val lonRequestBody: okhttp3.RequestBody? = currentLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())

                val requestBody = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                viewModelScope.launch {
                    try {
                        getTokenFromDataStore()

                        val apiServiceAuth = ApiConfig.getAuthService()
                        val userRepository = UserRepository.getInstance(userPreference, apiServiceAuth)
                        val apiService = ApiConfig.getStoryService(userRepository)

                        val response = apiService.uploadStory(
                            photo = multipartBody,
                            description = requestBody,
                            lat = latRequestBody,
                            lon = lonRequestBody
                        )

                        onSuccess(response.message ?: "Story uploaded successfully!")
                    } catch (e: HttpException) {
                        onError("Failed to upload story. Please try again.")
                    } catch (e: Exception) {
                        onError("An unexpected error occurred. Please try again.")
                    }
                }
            } catch (e: IOException) {
                onError("Failed to prepare the image. Please try again.")
            }
        }
    }

    private suspend fun getTokenFromDataStore(): String {
        val user = userPreference.getSession().first()
        return user.token
    }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun setLocation(location: Location) {
        currentLocation = location
    }
}
