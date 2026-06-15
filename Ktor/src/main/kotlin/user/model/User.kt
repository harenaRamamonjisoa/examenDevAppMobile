package com.tco.user.model

import com.tco.dto.response.UserResponse

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber : String,
    val studentId : String,
    val hashedPassword: String,
    val role : Role,
    val statut : Statut
)

enum class Role {
    STUDENT,
    BUREAU_MEMBRER,
    ADMIN
}

enum class Statut {
    PENDING,
    APPROVED,
    REFUSED
}

fun User.toResponse(): UserResponse = UserResponse(
    id = id,
    firstName = firstName,
    phoneNumber = phoneNumber,
    email = email,
    statut = statut
)


