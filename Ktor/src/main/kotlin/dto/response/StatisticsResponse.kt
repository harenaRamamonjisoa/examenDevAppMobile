package com.tco.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponse(
    val totalEvents: Int,
    val totalReservations: Int,
    val totalUsers: Int?
)
