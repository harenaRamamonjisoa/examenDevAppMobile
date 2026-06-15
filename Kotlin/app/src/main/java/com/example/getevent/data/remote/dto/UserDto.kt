package com.example.getevent.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    STUDENT,
    BUREAU_MEMBRER,
    ADMIN
}

@Serializable
enum class Statut {
    PENDING,
    APPROVED,
    REFUSED
}

@Serializable
data class UserResponse(
    val id: Long,
    val firstName: String,
    val email: String,
    val phoneNumber: String,
    val statut: Statut,
    val role: Role = Role.STUDENT // Added role
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserResponse
)
