package com.tco.dto.response

import com.tco.user.model.Statut
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val firstName: String,
    val email: String,
    val phoneNumber: String,
    val statut : com.tco.user.model.Statut
)