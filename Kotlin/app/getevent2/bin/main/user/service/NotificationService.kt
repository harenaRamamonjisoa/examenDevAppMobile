package com.tco.user.service

import com.tco.dto.request.UpdateStatusRequest

interface NotificationService {
    suspend fun sendRefusalNotification()
    suspend fun sendAutorisedNotification()
}