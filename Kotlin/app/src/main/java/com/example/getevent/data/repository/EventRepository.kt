package com.example.getevent.data.repository

import com.example.getevent.data.remote.ApiService
import com.example.getevent.data.remote.dto.ApiResponse
import com.example.getevent.data.remote.dto.EventDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllEvents(): Result<ApiResponse<List<EventDto>>> {
        return try {
            val response = apiService.getAllEvents()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventById(id: Long): Result<ApiResponse<EventDto>> {
        return try {
            val response = apiService.getEventById(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
