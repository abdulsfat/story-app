package com.submission.submissionstoryapp.view.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.submission.submissionstoryapp.R
import com.submission.submissionstoryapp.data.factory.ViewModelFactory
import com.submission.submissionstoryapp.databinding.ActivityMapsBinding
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.view.main.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        loadStoryLocations()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun setMapStyle() {
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        } catch (_: Resources.NotFoundException) {
        }
    }

    private fun loadStoryLocations() {
        lifecycleScope.launch {
            storyViewModel.fetchStoriesWithLocation().collect { stories ->
                addMarkers(stories, mMap)
            }
        }
    }

    private fun addMarkers(detailStoryItem: List<ListStoryItem>?, googleMap: GoogleMap) {
        lifecycleScope.launch(Dispatchers.Default) {
            if (detailStoryItem.isNullOrEmpty()) {
                withContext(Dispatchers.Main) {
                    val defaultLatLng = LatLng(-6.200000, 106.816666)
                    val defaultZoomLevel = 5f
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoomLevel))
                }
            } else {
                val markers = detailStoryItem.mapNotNull { story ->
                    val lat = story.lat.toString().toDoubleOrNull()
                    val lon = story.lon.toString().toDoubleOrNull()
                    if (lat != null && lon != null) {
                        MarkerOptions()
                            .position(LatLng(lat, lon))
                            .title(story.name ?: "No Name")
                            .snippet(story.description ?: "No Description")
                    } else {
                        null
                    }
                }

                withContext(Dispatchers.Main) {
                    markers.forEach { markerOptions ->
                        googleMap.addMarker(markerOptions)
                    }

                    detailStoryItem.firstOrNull { it.lat != null && it.lon != null }?.let { story ->
                        val firstLocation = LatLng(story.lat.toString().toDoubleOrNull()!!, story.lon.toString().toDoubleOrNull()!!)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 5f))
                    }
                }
            }
        }
    }
}
