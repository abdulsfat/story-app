package com.submission.submissionstoryapp.view.logout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.submission.submissionstoryapp.data.repository.UserRepository
import com.submission.submissionstoryapp.view.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(org.junit.runners.JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var mainViewModel: MainViewModel


    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        userRepository = mockk()

        coEvery { userRepository.logout() } just Runs

        mainViewModel = MainViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `when logout is called, user session is cleared`() = runBlocking {
        mainViewModel.logout()

        coVerify { userRepository.logout() }
    }
}
