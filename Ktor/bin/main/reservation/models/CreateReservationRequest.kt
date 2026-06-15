package reservation.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CreateReservationRequest(
    @Contextual
    val dateReservation: LocalDate,
    val idEvent: Long,
    val statut: StatutReservation
)