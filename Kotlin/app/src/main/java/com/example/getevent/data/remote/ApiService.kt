package com.example.getevent.data.remote

import com.example.getevent.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("user/register-student")
    suspend fun registerStudent(@Body request: RegisterRequest): UserResponse

    @POST("user/register-staff")
    suspend fun registerStaff(@Body request: RegisterRequest): UserResponse

    // User Profile
    @GET("me/status")
    suspend fun getMyStatus(): Map<String, String>

    @GET("me/reservations")
    suspend fun getMyReservations(): ApiResponse<List<ReservationDto>>

    // Events
    @GET("api/events")
    suspend fun getAllEvents(): ApiResponse<List<EventDto>>

    @GET("api/events/{idEvent}")
    suspend fun getEventById(@Path("idEvent") idEvent: Long): ApiResponse<EventDto>

    // Admin
    @GET("admin/statistics")
    suspend fun getStatistics(): StatisticsResponse

    @PATCH("admin/user/{id}/statut")
    suspend fun updateUserStatus(
        @Path("id") userId: Long,
        @Body request: UpdateStatusRequest
    ): Map<String, String>
}

@kotlinx.serialization.Serializable
data class StatisticsResponse(
    val totalEvents: Int,
    val totalReservations: Int,
    val totalUsers: Int?
)

@kotlinx.serialization.Serializable
data class UpdateStatusRequest(
    val statut: Statut
)
