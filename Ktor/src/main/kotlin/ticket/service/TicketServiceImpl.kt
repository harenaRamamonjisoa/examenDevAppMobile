package com.tco.ticket.service

import com.tco.ticket.models.CreateTicketRequest
import com.tco.ticket.models.Ticket
import com.tco.ticket.repository.TicketRepository
import reservation.models.StatutReservation
import reservation.repositories.ReservationRepository
import transaction.models.StatutPaiement
import transaction.repositories.TransactionRepository
import java.time.LocalDate
import java.util.UUID

class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val reservationRepository: ReservationRepository,
    private val transactionRepository: TransactionRepository
) : TicketService {

    override suspend fun getAllTickets(): List<Ticket> {
        return ticketRepository.findAll()
    }

    override suspend fun getTicketById(idTicket: Long): Result<Ticket> {
        val ticket = ticketRepository.findById(idTicket)
        return if (ticket != null) {
            Result.success(ticket)
        } else {
            Result.failure(TicketNotFoundException("Ticket $idTicket non trouvé"))
        }
    }

    override suspend fun getTicketByReservation(idReservation: Long): Result<Ticket> {
        val ticket = ticketRepository.findByReservation(idReservation)
        return if (ticket != null) {
            Result.success(ticket)
        } else {
            Result.failure(TicketNotFoundException("Aucun ticket pour la réservation $idReservation"))
        }
    }

    override suspend fun getTicketByUrlCode(urlCode: String): Result<Ticket> {
        val ticket = ticketRepository.findByUrlCode(urlCode)
        return if (ticket != null) {
            Result.success(ticket)
        } else {
            Result.failure(TicketNotFoundException("Ticket introuvable pour ce QR code"))
        }
    }

    override suspend fun generateTicket(request: CreateTicketRequest): Result<Ticket> {
        val reservation = reservationRepository.findById(request.idReservation)
            ?: return Result.failure(TicketValidationException(listOf("La réservation ${request.idReservation} n'existe pas")))

        if (reservation.statut != StatutReservation.PAYANT) {
            return Result.failure(
                TicketValidationException(listOf("Seules les réservations payantes génèrent un ticket"))
            )
        }

        val transaction = transactionRepository.findByReservation(request.idReservation)
            ?: return Result.failure(
                PaymentNotConfirmedException("Aucune transaction associée à cette réservation")
            )

        if (transaction.statutPaiement != StatutPaiement.CONFIRME) {
            return Result.failure(
                PaymentNotConfirmedException("Le paiement doit être confirmé avant la génération du ticket")
            )
        }

        ticketRepository.findByReservation(request.idReservation)?.let {
            return Result.failure(
                TicketAlreadyExistsException("Un ticket existe déjà pour cette réservation")
            )
        }

        val ticket = Ticket(
            idTicket = 0,
            urlCode = "getevent://ticket/${UUID.randomUUID()}",
            dateGeneration = LocalDate.now(),
            estUtilise = false,
            idReservation = request.idReservation
        )

        return Result.success(ticketRepository.save(ticket))
    }

    override suspend fun useTicket(urlCode: String): Result<Ticket> {
        val ticket = ticketRepository.findByUrlCode(urlCode)
            ?: return Result.failure(TicketNotFoundException("Ticket introuvable pour ce QR code"))

        if (ticket.estUtilise) {
            return Result.failure(TicketAlreadyUsedException("Ce ticket a déjà été utilisé"))
        }

        val updated = ticket.copy(estUtilise = true)
        val saved = ticketRepository.update(ticket.idTicket, updated)
            ?: return Result.failure(TicketUpdateException("Erreur lors de la validation du ticket"))

        return Result.success(saved)
    }

    override suspend fun deleteTicket(idTicket: Long): Result<Boolean> {
        ticketRepository.findById(idTicket)
            ?: return Result.failure(TicketNotFoundException("Ticket $idTicket non trouvé"))

        return Result.success(ticketRepository.delete(idTicket))
    }
}
