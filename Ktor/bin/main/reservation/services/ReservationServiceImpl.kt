package reservation.services

import event.repositories.EventRepository
import event.repositories.InMemoryEventRepository
import reservation.models.CreateReservationRequest
import reservation.models.Reservation
import reservation.models.UpdateReservationRequest
import reservation.repositories.InMemoryReservationRepository
import reservation.repositories.ReservationRepository

class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val eventRepository: EventRepository
) : ReservationService {

    override suspend fun getAllReservations(): List<Reservation> {
        return reservationRepository.findAll()
    }

    override suspend fun getReservationById(idReservation: Long): Result<Reservation> {
        val reservation = reservationRepository.findById(idReservation)
        return if (reservation != null) {
            Result.success(reservation)
        } else {
            Result.failure(ReservationNotFoundException("Réservation $idReservation non trouvée"))
        }
    }

    override suspend fun getReservationsByEvent(idEvent: Long): List<Reservation> {
        return reservationRepository.findByEvent(idEvent)
    }

    override suspend fun createReservation(request: CreateReservationRequest): Result<Reservation> {
        // Vérifier que l'événement existe
        val event = eventRepository.findById(request.idEvent)
            ?: return Result.failure(EventNotAvailableException("L'événement ${request.idEvent} n'existe pas"))

        // Vérifier la capacité
        if (event.nombreParticipants >= event.lieu.capacite) {
            return Result.failure(EventNotAvailableException("L'événement est complet !"))
        }

        validateCreateRequest(request)

        val reservation = Reservation(
            idReservation = 0,
            dateReservation = request.dateReservation,
            idEvent = request.idEvent,
            statut = request.statut
        )

        val savedReservation = reservationRepository.save(reservation)

        // INCRÉMENTER le nombre de participants
        val updatedEvent = event.copy(nombreParticipants = event.nombreParticipants + 1)
        eventRepository.update(event.idEvent, updatedEvent)

        return Result.success(savedReservation)
    }

    override suspend fun updateReservation(idReservation: Long, request: UpdateReservationRequest): Result<Reservation> {
        val existing = reservationRepository.findById(idReservation)
            ?: return Result.failure(ReservationNotFoundException("Réservation $idReservation non trouvée"))

        // Si on change l'événement, vérifier qu'il existe
        if (request.idEvent != null) {
            eventRepository.findById(request.idEvent)
                ?: return Result.failure(EventNotAvailableException("L'événement ${request.idEvent} n'existe pas"))
        }

        val updated = existing.copy(
            dateReservation = request.dateReservation ?: existing.dateReservation,
            idEvent = request.idEvent ?: existing.idEvent,
            statut = request.statut ?: existing.statut
        )

        val result = reservationRepository.update(idReservation, updated)
        return if (result != null) {
            Result.success(result)
        } else {
            Result.failure(ReservationUpdateException("Erreur lors de la mise à jour"))
        }
    }

    override suspend fun deleteReservation(idReservation: Long): Result<Boolean> {
        val reservation = reservationRepository.findById(idReservation)
            ?: return Result.failure(ReservationNotFoundException("Réservation $idReservation non trouvée"))

        val deleted = reservationRepository.delete(idReservation)

        if (deleted) {
            // DÉCRÉMENTER le nombre de participants
            val event = eventRepository.findById(reservation.idEvent)
            if (event != null) {
                val newCount = if (event.nombreParticipants > 0) event.nombreParticipants - 1 else 0
                val updatedEvent = event.copy(nombreParticipants = newCount)
                eventRepository.update(event.idEvent, updatedEvent)
            }
        }

        return Result.success(deleted)
    }

    private fun validateCreateRequest(request: CreateReservationRequest) {
        val errors = mutableListOf<String>()

        if (request.idEvent <= 0) {
            errors.add("L'ID de l'événement est invalide")
        }

        if (errors.isNotEmpty()) {
            throw ReservationValidationException(errors)
        }
    }
}