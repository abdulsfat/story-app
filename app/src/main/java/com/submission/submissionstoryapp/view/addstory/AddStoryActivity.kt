package com.submission.submissionstoryapp.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.submission.submissionstoryapp.R
import com.submission.submissionstoryapp.data.network.api.ApiConfig
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.databinding.ActivityAddStoryBinding
import com.submission.submissionstoryapp.utils.UserPreference
import com.submission.submissionstoryapp.utils.dataStore
import com.submission.submissionstoryapp.utils.getImageUri
import com.submission.submissionstoryapp.utils.reduceFileImage
import com.submission.submissionstoryapp.utils.uriToFile
import com.submission.submissionstoryapp.view.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException


class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var userPreference: UserPreference
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        userPreference = UserPreference.getInstance(applicationContext.dataStore)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let {
            outState.putString("currentImageUri", it.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedUri = savedInstanceState.getString("currentImageUri")
        if (!savedUri.isNullOrEmpty()) {
            currentImageUri = Uri.parse(savedUri)
            showImage()
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        val descriptionText = binding.etStoryDescription.text.toString().trim()
        if (descriptionText.isEmpty()) {
            showToast(getString(R.string.error_empty_description))
            return
        }

        if (currentImageUri == null) {
            showToast(getString(R.string.error_empty_image))
            return
        }

        currentImageUri?.let { uri ->
            try {
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image Info", "File: ${imageFile.path}, Size: ${imageFile.length()} bytes")

                val latRequestBody: okhttp3.RequestBody? = null
                val lonRequestBody: okhttp3.RequestBody? = null

                val requestBody = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                lifecycleScope.launch {
                    try {
                        getTokenFromDataStore()

                        val pref = UserPreference.getInstance(dataStore)
                        val apiServiceAuth = ApiConfig.getAuthService()

                        val userRepository = UserRepository.getInstance(pref, apiServiceAuth)
                        val apiService = ApiConfig.getStoryService(userRepository)


                        val response = apiService.uploadStory(
                            photo = multipartBody,
                            description = requestBody,
                            lat = latRequestBody,
                            lon = lonRequestBody
                        )
                        showLoading(false)
                        showToast(response.message ?: "Story uploaded successfully!")

                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } catch (e: HttpException) {
                        showLoading(false)
                        Log.e("Upload Error", "Error: ${e.response()?.errorBody()?.string()}")
                        showToast("Failed to upload story. Please try again.")
                    } catch (e: Exception) {
                        showLoading(false)
                        Log.e("Exception", "Error: ${e.localizedMessage}")
                        showToast("An unexpected error occurred. Please try again.")
                    }
                }
            } catch (e: IOException) {
                showLoading(false)
                showToast("Failed to prepare the image. Please try again.")
            }
        }
    }

    private suspend fun getTokenFromDataStore(): String {
        val user = userPreference.getSession().first()
        return user.token
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
