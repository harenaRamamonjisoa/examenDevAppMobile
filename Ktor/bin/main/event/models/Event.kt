package event.models

import java.time.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val idEvent: Long,
    val nomEvent: String,
    @Contextual
    val dateEvent: LocalDate,
    val lieu: Lieu,
    val description: String,
    val nombreParticipants: Int = 0
)