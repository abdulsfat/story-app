package com.submission.submissionstoryapp.view.register


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.submission.submissionstoryapp.api.ApiConfig
import com.submission.submissionstoryapp.data.model.SignupResponse
import com.submission.submissionstoryapp.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val edtname = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val edtemail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val edtpass = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(title, name, edtname, email, edtemail, pass, edtpass, signup)
            start()
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



    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Validasi input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showToast("Semua kolom harus diisi")
                return@setOnClickListener
            }

            // Mengirimkan data untuk signup
            signupUser(name, email, password)
        }
    }

    private fun signupUser(name: String, email: String, password: String) {
        // Menampilkan loading (implementasi showLoading bisa ditambahkan di sini jika perlu)
        // showLoading(true)

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.register(name, email, password)

                if (response.error == false) {
                    showSuccessDialog(email)
                } else {
                    showToast(response.message ?: "Terjadi kesalahan.")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                if (errorBody != null) {
                    Log.e("SignupError", errorBody)
                    val errorResponse = Gson().fromJson(errorBody, SignupResponse::class.java)
                    if (errorResponse.message?.contains("Email is already taken", true) == true) {
                        showToast("Email sudah terdaftar. Silakan gunakan email lain.")
                    } else {
                        showToast(errorResponse.message ?: "Terjadi kesalahan.")
                    }
                } else {
                    showToast("Terjadi kesalahan saat koneksi ke server.")
                }
            } catch (e: Exception) {
                showToast("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }


    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
            setPositiveButton("Lanjut") { _, _ ->
                finish() // Menutup activity
            }
            create()
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}