package com.submission.submissionstoryapp.view.addstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.submission.submissionstoryapp.R
import com.submission.submissionstoryapp.databinding.ActivityAddStoryBinding
import com.submission.submissionstoryapp.utils.getImageUri
import com.submission.submissionstoryapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()

    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadStory() }

        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Jika Switch ON, minta izin lokasi
                if (viewModel.checkLocationPermission(this)) {
                    getLocation()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                currentLocation = null
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        currentImageUri = uri
        showImage()
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri!!)
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadStory() {
        val descriptionText = binding.etStoryDescription.text.toString().trim()

        viewModel.uploadStory(
            descriptionText,
            currentImageUri,
            currentLocation,
            onSuccess = { message ->
                showToast(message)
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
                finish()
            },
            onError = { error ->
                showToast(error)
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkLocationServices()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it
                    viewModel.setLocation(it)
                }
            }
        } else {
            showToast(getString(R.string.error_location_services))
        }
    }

    private fun checkLocationServices(): Boolean {
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                binding.locationSwitch.isChecked = false
            }
        }
}
