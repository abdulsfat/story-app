package com.submission.submissionstoryapp.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.submission.submissionstoryapp.databinding.ActivityDetailBinding
import com.submission.submissionstoryapp.api.ApiConfig
import com.submission.submissionstoryapp.utils.UserPreference
import com.submission.submissionstoryapp.utils.dataStore
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlinx.coroutines.flow.first

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var storyId: String? = null
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(applicationContext.dataStore)

        storyId = intent.getStringExtra("story_id")

        if (storyId != null) {
            lifecycleScope.launch {
                val token = getTokenFromDataStore()
                if (token.isNotEmpty()) {
                    fetchStoryDetail(storyId!!, token)
                } else {
                    Toast.makeText(this@DetailActivity, "Token not found!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(this, "Story ID not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun getTokenFromDataStore(): String {
        val user = userPreference.getSession().first()
        return user.token
    }

    private fun fetchStoryDetail(storyId: String, token: String) {
        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getStoryDetail(storyId)

//                Log.d("DetailActivity", "Response: $response")

                if (response.story != null) {
                    val story = response.story

                    binding.storyTitle.text = story.name
                    binding.storyDescription.text = story.description
                    binding.storyDate.text = story.createdAt

                    Glide.with(this@DetailActivity)
                        .load(story.photoUrl)
                        .into(binding.ivStoryImage)
                } else {
                    Toast.makeText(this@DetailActivity, "Story not found!", Toast.LENGTH_SHORT)
                        .show()
                }

            } catch (e: HttpException) {
                e.printStackTrace()
                Toast.makeText(this@DetailActivity, "Failed to fetch details", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@DetailActivity,
                    "Error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}