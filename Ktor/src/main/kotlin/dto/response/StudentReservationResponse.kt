package com.tco.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import reservation.models.StatutPaiementReservation
import reservation.models.StatutReservation
import java.time.LocalDate

@Serializable
data class StudentReservationResponse(
    val idReservation: Long,
    @Contextual
    val dateReservation: LocalDate,
    val idEvent: Long,
    val nomEvent: String,
    @Contextual
    val dateEvent: LocalDate,
    val estPrive: Boolean,
    val statutReservation: StatutReservation,
    val statutPaiement: StatutPaiementReservation,
    val montant: Double? = null,
    val idTransaction: Long? = null
)
