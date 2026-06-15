package reservation.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Reservation(
    val idReservation: Long,
    @Contextual
    val dateReservation: LocalDate,
    val idEvent: Long,
    val statut: StatutReservation
)