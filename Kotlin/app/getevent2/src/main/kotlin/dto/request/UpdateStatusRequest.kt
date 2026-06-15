package com.tco.dto.request

import com.tco.user.model.Statut
import kotlinx.serialization.Serializable


@Serializable
data class UpdateStatusRequest(
    val statut : com.tco.user.model.Statut
)
