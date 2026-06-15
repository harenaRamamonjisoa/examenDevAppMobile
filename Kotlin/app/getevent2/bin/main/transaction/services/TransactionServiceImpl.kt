package transaction.services

import com.tco.ticket.models.CreateTicketRequest
import com.tco.ticket.service.TicketService
import reservation.models.StatutReservation
import reservation.repositories.ReservationRepository
import transaction.models.CreateTransactionRequest
import transaction.models.StatutPaiement
import transaction.models.Transaction
import transaction.models.UpdateTransactionRequest
import transaction.repositories.TransactionRepository
import java.time.Instant

class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val reservationRepository: ReservationRepository,
    private val ticketService: TicketService
) : TransactionService {

    override suspend fun getAllTransactions(): List<Transaction> {
        return transactionRepository.findAll()
    }

    override suspend fun getTransactionById(idTransaction: Long): Result<Transaction> {
        val transaction = transactionRepository.findById(idTransaction)
        return if (transaction != null) {
            Result.success(transaction)
        } else {
            Result.failure(TransactionNotFoundException("Transaction $idTransaction non trouvée"))
        }
    }

    override suspend fun getTransactionByReservation(idReservation: Long): Result<Transaction> {
        val transaction = transactionRepository.findByReservation(idReservation)
        return if (transaction != null) {
            Result.success(transaction)
        } else {
            Result.failure(TransactionNotFoundException("Aucune transaction pour la réservation $idReservation"))
        }
    }

    override suspend fun createTransaction(request: CreateTransactionRequest): Result<Transaction> {
        val reservation = reservationRepository.findById(request.idReservation)
            ?: return Result.failure(ReservationNotLinkedException("La réservation ${request.idReservation} n'existe pas"))

        if (reservation.statut != StatutReservation.PAYANT) {
            return Result.failure(
                TransactionValidationException(listOf("Cette réservation ne nécessite pas de paiement"))
            )
        }

        transactionRepository.findByReservation(request.idReservation)?.let {
            return Result.failure(
                TransactionAlreadyExistsException("Une transaction existe déjà pour cette réservation")
            )
        }

        validateCreateRequest(request)

        val transaction = Transaction(
            idTransaction = 0,
            montant = request.montant,
            dateTransaction = Instant.now(),
            modePaiement = request.modePaiement,
            statutPaiement = StatutPaiement.EN_ATTENTE,
            referencePaiement = request.referencePaiement,
            idReservation = request.idReservation
        )

        return Result.success(transactionRepository.save(transaction))
    }

    override suspend fun updateTransaction(
        idTransaction: Long,
        request: UpdateTransactionRequest
    ): Result<Transaction> {
        val existing = transactionRepository.findById(idTransaction)
            ?: return Result.failure(TransactionNotFoundException("Transaction $idTransaction non trouvée"))

        val updated = existing.copy(
            montant = request.montant ?: existing.montant,
            modePaiement = request.modePaiement ?: existing.modePaiement,
            statutPaiement = request.statutPaiement ?: existing.statutPaiement,
            referencePaiement = request.referencePaiement ?: existing.referencePaiement
        )

        val result = transactionRepository.update(idTransaction, updated)
        return if (result != null) {
            Result.success(result)
        } else {
            Result.failure(TransactionUpdateException("Erreur lors de la mise à jour de la transaction"))
        }
    }

    override suspend fun confirmTransaction(idTransaction: Long): Result<Transaction> {
        val existing = transactionRepository.findById(idTransaction)
            ?: return Result.failure(TransactionNotFoundException("Transaction $idTransaction non trouvée"))

        if (existing.statutPaiement == StatutPaiement.CONFIRME) {
            return Result.success(existing)
        }

        if (existing.statutPaiement != StatutPaiement.EN_ATTENTE) {
            return Result.failure(
                TransactionValidationException(listOf("Seule une transaction en attente peut être confirmée"))
            )
        }

        val confirmed = existing.copy(statutPaiement = StatutPaiement.CONFIRME)
        val saved = transactionRepository.update(idTransaction, confirmed)
            ?: return Result.failure(TransactionUpdateException("Erreur lors de la confirmation du paiement"))

        ticketService.generateTicket(CreateTicketRequest(idReservation = saved.idReservation))
            .onFailure { error ->
                return Result.failure(error)
            }

        return Result.success(saved)
    }

    override suspend fun deleteTransaction(idTransaction: Long): Result<Boolean> {
        transactionRepository.findById(idTransaction)
            ?: return Result.failure(TransactionNotFoundException("Transaction $idTransaction non trouvée"))

        return Result.success(transactionRepository.delete(idTransaction))
    }

    private fun validateCreateRequest(request: CreateTransactionRequest) {
        val errors = mutableListOf<String>()

        if (request.idReservation <= 0) {
            errors.add("L'ID de la réservation est invalide")
        }
        if (request.montant <= 0) {
            errors.add("Le montant doit être supérieur à zéro")
        }
        if (request.modePaiement.isBlank()) {
            errors.add("Le mode de paiement est obligatoire")
        }
        if (request.referencePaiement <= 0) {
            errors.add("La référence de paiement est invalide")
        }

        if (errors.isNotEmpty()) {
            throw TransactionValidationException(errors)
        }
    }
}
