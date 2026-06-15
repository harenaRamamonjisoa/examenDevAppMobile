package event.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEventRequest(
    val nomEvent: String?=null,
    val dateEvent: String? = null,
    val lieu: Lieu? = null,
    val description: String? = null,
    val nombreParticipants: Int? = null
)