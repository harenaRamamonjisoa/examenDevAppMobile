package com.example.getevent.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val studentId: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
