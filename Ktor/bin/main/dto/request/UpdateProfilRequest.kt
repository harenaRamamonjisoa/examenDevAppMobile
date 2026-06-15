package com.tco.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfilRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val studentId : String
)
