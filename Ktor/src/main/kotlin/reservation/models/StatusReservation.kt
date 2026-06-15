package reservation.models

import kotlinx.serialization.Serializable

@Serializable
enum class StatutReservation {
    PAYANT,
    NON_PAYANT
}