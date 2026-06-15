package com.tco.ticket.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Ticket(
    val idTicket: Long,
    val urlCode: String,
    @Contextual
    val dateGeneration: LocalDate,
    val estUtilise: Boolean,
    val idReservation: Long
)
