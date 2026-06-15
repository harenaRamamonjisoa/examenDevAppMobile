package com.example.getevent.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LieuDto(
    val nom: String,
    val longitude: Float,
    val latitude: Float,
    val capacite: Int
)

@Serializable
data class EventDto(
    val idEvent: Long,
    val nomEvent: String,
    val dateEvent: String, 
    val lieu: LieuDto,
    val description: String,
    val nombreParticipants: Int,
    val estPrive: Boolean
)

@Serializable
data class ReservationDto(
    val idReservation: Long,
    val dateReservation: String,
    val idEvent: Long,
    val nomEvent: String,
    val dateEvent: String,
    val estPrive: Boolean,
    val statutReservation: String,
    val statutPaiement: String,
    val montant: Double? = null,
    val idTransaction: Long? = null
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
