package reservation.repositories

import reservation.models.Reservation

interface ReservationRepository {
    suspend fun findAll(): List<Reservation>
    suspend fun findById(idReservation: Long): Reservation?
    suspend fun save(reservation: Reservation): Reservation
    suspend fun update(idReservation: Long, reservation: Reservation): Reservation?
    suspend fun delete(idReservation: Long): Boolean
    suspend fun findByEvent(idEvent: Long): List<Reservation>
    suspend fun findByUserId(idUser: Long): List<Reservation>
}