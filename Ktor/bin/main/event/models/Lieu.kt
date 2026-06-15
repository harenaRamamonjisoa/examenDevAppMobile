package event.models

import kotlinx.serialization.Serializable

@Serializable
data class Lieu (
    val nom: String,
    val longitude: Float,
    val latitude: Float,
    val capacite: Int
)