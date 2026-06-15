package com.example.getevent.data.repository

import com.example.getevent.data.remote.ApiService
import com.example.getevent.data.remote.StatisticsResponse
import com.example.getevent.data.remote.UpdateStatusRequest
import com.example.getevent.data.remote.dto.Statut
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatistics(): Result<StatisticsResponse> {
        return try {
            val response = apiService.getStatistics()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserStatus(userId: Long, statut: Statut): Result<Map<String, String>> {
        return try {
            val response = apiService.updateUserStatus(userId, UpdateStatusRequest(statut))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
