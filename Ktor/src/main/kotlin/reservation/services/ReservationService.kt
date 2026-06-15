package reservation.services

import com.tco.dto.response.AdminStudentReservationResponse
import com.tco.dto.response.StudentReservationResponse
import reservation.models.CreateReservationRequest
import reservation.models.FiltrePaiement
import reservation.models.Reservation
import reservation.models.UpdateReservationRequest

interface ReservationService {
    suspend fun getAllReservations(): List<Reservation>
    suspend fun getReservationById(idReservation: Long): Result<Reservation>
    suspend fun getReservationsByEvent(idEvent: Long): List<Reservation>
    suspend fun getReservationsByUser(idUser: Long): List<StudentReservationResponse>
    suspend fun getEventReservationsForAdmin(idEvent: Long, filtre: FiltrePaiement): List<AdminStudentReservationResponse>
    suspend fun createReservation(request: CreateReservationRequest, idUser: Long): Result<Reservation>
    suspend fun updateReservation(idReservation: Long, request: UpdateReservationRequest): Result<Reservation>
    suspend fun deleteReservation(idReservation: Long): Result<Boolean>
}
