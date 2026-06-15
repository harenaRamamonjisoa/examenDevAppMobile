package event.models

import java.time.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(
    //tsy mila asiana ID intsony
    val nomEvent: String,
    @Contextual
    val dateEvent: LocalDate,
    val lieu: Lieu,
    val description: String,
    val nombreParticipants: Int = 0,
    val estPrive: Boolean = false
)