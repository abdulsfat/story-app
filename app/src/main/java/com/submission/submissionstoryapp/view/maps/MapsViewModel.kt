package com.submission.submissionstoryapp.view.maps

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.submission.submissionstoryapp.data.network.story.ListStoryItem
import com.submission.submissionstoryapp.view.main.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MapsViewModel(private val storyViewModel: StoryViewModel) : ViewModel() {

    fun fetchStoryLocations(): Flow<List<ListStoryItem>?> {
        return storyViewModel.fetchStoriesWithLocation()
    }

    suspend fun createMarkersFromStories(
        detailStoryItem: List<ListStoryItem>?,
        googleMap: GoogleMap
    ) {
        if (detailStoryItem.isNullOrEmpty()) {
            val jakartaLatLng = LatLng(-6.2088, 106.8456)
            val defaultZoomLevel = 5f
            withContext(Dispatchers.Main) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakartaLatLng, defaultZoomLevel))
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
                val jakartaLatLng = LatLng(-6.2088, 106.8456)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakartaLatLng, 5f))
            }
        }
    }
}
