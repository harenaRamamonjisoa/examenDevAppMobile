package reservation.models

import kotlinx.serialization.Serializable

@Serializable
enum class StatutPaiementReservation {
    GRATUIT,
    NON_PAYE,
    EN_ATTENTE,
    PAYE
}
