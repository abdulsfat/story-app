package com.submission.submissionstoryapp.view.login

import android.os.Looper
import androidx.lifecycle.Observer
import com.submission.submissionstoryapp.data.model.UserModel
import com.submission.submissionstoryapp.data.network.authentication.LoginResponse
import com.submission.submissionstoryapp.data.network.authentication.LoginResult
import com.submission.submissionstoryapp.data.repository.UserRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var userRepository: UserRepository

    private lateinit var loginViewModel: LoginViewModel

    private val loginObserver: Observer<UserModel?> = mockk(relaxed = true)
    private val errorObserver: Observer<String?> = mockk(relaxed = true)

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(testDispatcher)

        userRepository = mockk()
        loginViewModel = LoginViewModel(userRepository)

        mockkStatic(Looper::class)
        val mainLooper = mockk<Looper>()
        every { Looper.getMainLooper() } returns mainLooper
        every { mainLooper.thread } returns Thread.currentThread()

        loginViewModel.loginResult.observeForever(loginObserver)
        loginViewModel.errorMessage.observeForever(errorObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()


        loginViewModel.loginResult.removeObserver(loginObserver)
        loginViewModel.errorMessage.removeObserver(errorObserver)

        unmockkStatic(Looper::class)

        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when login is successful, user session is saved`() = runBlockingTest {
        val email = "user@example.com"
        val password = "password123"
        val mockResponse = LoginResponse(
            error = false,
            message = "Login Success",
            loginResult = LoginResult(
                userId = "sample-user-id",
                name = "sample-user-name",
                token = "sample-token"
            )
        )

        coEvery { userRepository.login(email, password) } returns mockResponse

        loginViewModel.login(email, password)

        verify { loginObserver.onChanged(any()) }
        val capturedUser = slot<UserModel>()
        verify { loginObserver.onChanged(capture(capturedUser)) }
        assertNotNull(capturedUser.captured)
        assertEquals(email, capturedUser.captured.email)
        assertEquals("sample-token", capturedUser.captured.token)
    }

    @Test
    fun `when login fails with 401 error, show Unauthorized error`() = runBlockingTest {
        val email = "user@example.com"
        val password = "wrongpassword"
        val mockException = mockk<HttpException>()
        coEvery { mockException.code() } returns 401
        coEvery { userRepository.login(email, password) } throws mockException

        loginViewModel.login(email, password)

        verify { errorObserver.onChanged(any()) }
        val capturedError = slot<String>()
        verify { errorObserver.onChanged(capture(capturedError)) }
        assertEquals("401 Unauthorized", capturedError.captured)
    }

    @Test
    fun `when login fails with unknown error, show error message`() = runBlockingTest {
        val email = "user@example.com"
        val password = "wrongpassword"
        val mockErrorMessage = "Unknown error"
        coEvery { userRepository.login(email, password) } throws RuntimeException(mockErrorMessage)

        loginViewModel.login(email, password)

        verify { errorObserver.onChanged(any()) }
        val capturedError = slot<String>()
        verify { errorObserver.onChanged(capture(capturedError)) }
        assertEquals("Terjadi kesalahan: $mockErrorMessage", capturedError.captured)
    }
}
