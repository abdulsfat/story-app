package com.submission.submissionstoryapp.data.model

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val token: String?
)
