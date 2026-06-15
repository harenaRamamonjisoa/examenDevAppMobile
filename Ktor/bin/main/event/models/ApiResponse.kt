package event.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errors: List<String>? = null
)