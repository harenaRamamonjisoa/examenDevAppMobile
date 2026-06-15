package reservation.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpdateReservationRequest(
    @Contextual
    val dateReservation: LocalDate? = null,
    val idEvent: Long? = null,
    val statut: StatutReservation? = null
)