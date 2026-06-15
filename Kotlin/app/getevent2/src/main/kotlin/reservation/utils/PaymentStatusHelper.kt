package reservation.utils

import reservation.models.FiltrePaiement
import reservation.models.Reservation
import reservation.models.StatutPaiementReservation
import reservation.models.StatutReservation
import transaction.models.StatutPaiement
import transaction.models.Transaction

fun computeStatutPaiement(reservation: Reservation, transaction: Transaction?): StatutPaiementReservation {
    if (reservation.statut == StatutReservation.NON_PAYANT) {
        return StatutPaiementReservation.GRATUIT
    }
    return when (transaction?.statutPaiement) {
        null -> StatutPaiementReservation.NON_PAYE
        StatutPaiement.EN_ATTENTE -> StatutPaiementReservation.EN_ATTENTE
        StatutPaiement.CONFIRME -> StatutPaiementReservation.PAYE
        StatutPaiement.ECHOUE, StatutPaiement.ANNULE -> StatutPaiementReservation.NON_PAYE
    }
}

fun matchesFiltre(statut: StatutPaiementReservation, filtre: FiltrePaiement): Boolean = when (filtre) {
    FiltrePaiement.TOUS -> true
    FiltrePaiement.NON_PAYE -> statut == StatutPaiementReservation.NON_PAYE
    FiltrePaiement.EN_ATTENTE -> statut == StatutPaiementReservation.EN_ATTENTE
    FiltrePaiement.PAYE -> statut == StatutPaiementReservation.PAYE || statut == StatutPaiementReservation.GRATUIT
}
