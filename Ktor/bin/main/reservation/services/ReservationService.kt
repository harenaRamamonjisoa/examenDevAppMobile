package reservation.services

import reservation.models.CreateReservationRequest
import reservation.models.Reservation
import reservation.models.UpdateReservationRequest

interface ReservationService {
    suspend fun getAllReservations(): List<Reservation>
    suspend fun getReservationById(idReservation: Long): Result<Reservation>
    suspend fun getReservationsByEvent(idEvent: Long): List<Reservation>
    suspend fun createReservation(request: CreateReservationRequest): Result<Reservation>
    suspend fun updateReservation(idReservation: Long, request: UpdateReservationRequest): Result<Reservation>
    suspend fun deleteReservation(idReservation: Long): Result<Boolean>
}