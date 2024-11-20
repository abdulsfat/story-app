package com.submission.submissionstoryapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.submissionstoryapp.databinding.ActivityMainBinding
import com.submission.submissionstoryapp.view.welcome.WelcomeActivity
import com.submission.submissionstoryapp.viewmodel.MainViewModel
import com.submission.submissionstoryapp.viewmodel.StoryViewModel
import com.submission.submissionstoryapp.viewmodel.ViewModelFactory
import com.submission.submissionstoryapp.view.adapter.StoryAdapter
import com.submission.submissionstoryapp.view.detail.DetailActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val storyViewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupRecyclerView()
        observeStories()

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        storyViewModel.fetchStories()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        adapter.onItemClick = { story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("story_id", story.id)
            startActivity(intent)
        }
    }

    private fun observeStories() {
        lifecycleScope.launchWhenStarted {
            storyViewModel.stories.collect { stories ->
                adapter.submitList(stories)
            }
        }

        lifecycleScope.launchWhenStarted {
            storyViewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            mainViewModel.logout()
        }
    }

}
