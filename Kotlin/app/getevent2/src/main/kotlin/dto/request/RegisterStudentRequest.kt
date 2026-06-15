package com.tco.dto.request

import kotlinx.serialization.Serializable


@Serializable
data class RegisterStudentRequest(
    val firstName: String,
    val lastName: String,
    val email : String,
    val phoneNumber : String,
    val studentId : String,
    val password : String,
)
