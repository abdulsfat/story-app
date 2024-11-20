package com.submission.submissionstoryapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.submissionstoryapp.R
import com.submission.submissionstoryapp.databinding.ActivityMainBinding
import com.submission.submissionstoryapp.view.welcome.WelcomeActivity
import com.submission.submissionstoryapp.viewmodel.MainViewModel
import com.submission.submissionstoryapp.viewmodel.StoryViewModel
import com.submission.submissionstoryapp.viewmodel.ViewModelFactory
import com.submission.submissionstoryapp.view.adapter.StoryAdapter
import com.submission.submissionstoryapp.view.addstory.AddStoryActivity
import com.submission.submissionstoryapp.view.detail.DetailActivity
import java.util.Locale

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
        setupToolbar()
        setupRecyclerView()
        observeStories()
        loadStories()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        storyViewModel.fetchStories()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.logout -> {
                mainViewModel.logout()
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            R.id.change_language -> {
                showLanguageSelectionDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
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

    private fun loadStories() {
        lifecycleScope.launchWhenStarted {
            storyViewModel.stories.collect { stories ->
                adapter.submitList(stories)
            }
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(getString(R.string.language_english), getString(R.string.language_indonesian))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_language))
            .setItems(languages) { _, which ->
                when (which) {
                    0 -> setLanguage("en")
                    1 -> setLanguage("id")
                }
            }
            .show()
    }

    private fun setLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }
}
