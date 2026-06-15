package com.tco.dto.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import reservation.models.StatutPaiementReservation
import reservation.models.StatutReservation
import java.time.LocalDate

@Serializable
data class EtudiantInfo(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val studentId: String
)

@Serializable
data class AdminStudentReservationResponse(
    val idReservation: Long,
    @Contextual
    val dateReservation: LocalDate,
    val statutReservation: StatutReservation,
    val statutPaiement: StatutPaiementReservation,
    val montant: Double? = null,
    val idTransaction: Long? = null,
    val etudiant: EtudiantInfo
)
