package com.submission.submissionstoryapp.data.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.submission.submissionstoryapp.data.repository.StoryRepository
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.di.Injection
import com.submission.submissionstoryapp.view.login.LoginViewModel
import com.submission.submissionstoryapp.view.main.ListStoryViewModel
import com.submission.submissionstoryapp.view.main.MainViewModel
import com.submission.submissionstoryapp.view.main.StoryViewModel
import com.submission.submissionstoryapp.view.register.RegisterViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                ListStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(application, storyRepository, userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideUserRepository(application),
                        Injection.provideStoryRepository(application),
                        application
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

}
