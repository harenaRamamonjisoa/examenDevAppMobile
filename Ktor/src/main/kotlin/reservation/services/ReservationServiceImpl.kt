package reservation.services

import com.tco.dto.response.AdminStudentReservationResponse
import com.tco.dto.response.EtudiantInfo
import com.tco.dto.response.StudentReservationResponse
import com.tco.user.repository.UserRepository
import event.repositories.EventRepository
import reservation.models.CreateReservationRequest
import reservation.models.FiltrePaiement
import reservation.models.Reservation
import reservation.models.UpdateReservationRequest
import reservation.repositories.ReservationRepository
import reservation.utils.computeStatutPaiement
import reservation.utils.matchesFiltre
import transaction.repositories.TransactionRepository

class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val eventRepository: EventRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
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

    override suspend fun getReservationsByUser(idUser: Long): List<StudentReservationResponse> {
        return reservationRepository.findByUserId(idUser).map { reservation ->
            val event = eventRepository.findById(reservation.idEvent)
                ?: throw EventNotAvailableException("L'événement ${reservation.idEvent} n'existe pas")
            val transaction = transactionRepository.findByReservation(reservation.idReservation)
            val statutPaiement = computeStatutPaiement(reservation, transaction)

            StudentReservationResponse(
                idReservation = reservation.idReservation,
                dateReservation = reservation.dateReservation,
                idEvent = reservation.idEvent,
                nomEvent = event.nomEvent,
                dateEvent = event.dateEvent,
                estPrive = event.estPrive,
                statutReservation = reservation.statut,
                statutPaiement = statutPaiement,
                montant = transaction?.montant,
                idTransaction = transaction?.idTransaction
            )
        }
    }

    override suspend fun getEventReservationsForAdmin(
        idEvent: Long,
        filtre: FiltrePaiement
    ): List<AdminStudentReservationResponse> {
        val event = eventRepository.findById(idEvent)
            ?: throw EventNotAvailableException("L'événement $idEvent n'existe pas")

        if (!event.estPrive) {
            throw IllegalArgumentException("Cet endpoint est réservé aux événements privés")
        }

        return reservationRepository.findByEvent(idEvent).mapNotNull { reservation ->
            val transaction = transactionRepository.findByReservation(reservation.idReservation)
            val statutPaiement = computeStatutPaiement(reservation, transaction)

            if (!matchesFiltre(statutPaiement, filtre)) return@mapNotNull null

            val user = if (reservation.idUser > 0) {
                userRepository.findById(reservation.idUser)
            } else {
                null
            }

            AdminStudentReservationResponse(
                idReservation = reservation.idReservation,
                dateReservation = reservation.dateReservation,
                statutReservation = reservation.statut,
                statutPaiement = statutPaiement,
                montant = transaction?.montant,
                idTransaction = transaction?.idTransaction,
                etudiant = user?.let {
                    EtudiantInfo(
                        id = it.id,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        email = it.email,
                        studentId = it.studentId
                    )
                } ?: EtudiantInfo(
                    id = 0,
                    firstName = "Inconnu",
                    lastName = "",
                    email = "",
                    studentId = ""
                )
            )
        }
    }

    override suspend fun createReservation(request: CreateReservationRequest, idUser: Long): Result<Reservation> {
        val event = eventRepository.findById(request.idEvent)
            ?: return Result.failure(EventNotAvailableException("L'événement ${request.idEvent} n'existe pas"))

        if (event.nombreParticipants >= event.lieu.capacite) {
            return Result.failure(EventNotAvailableException("L'événement est complet !"))
        }

        validateCreateRequest(request)

        val reservation = Reservation(
            idReservation = 0,
            dateReservation = request.dateReservation,
            idEvent = request.idEvent,
            idUser = idUser,
            statut = request.statut
        )

        val savedReservation = reservationRepository.save(reservation)

        val updatedEvent = event.copy(nombreParticipants = event.nombreParticipants + 1)
        eventRepository.update(event.idEvent, updatedEvent)

        return Result.success(savedReservation)
    }

    override suspend fun updateReservation(idReservation: Long, request: UpdateReservationRequest): Result<Reservation> {
        val existing = reservationRepository.findById(idReservation)
            ?: return Result.failure(ReservationNotFoundException("Réservation $idReservation non trouvée"))

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
