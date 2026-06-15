package com.tco.ticket.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateTicketRequest(
    val idReservation: Long
)
