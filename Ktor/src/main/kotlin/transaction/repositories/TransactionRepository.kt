package transaction.repositories

import transaction.models.Transaction

interface TransactionRepository {
    suspend fun findAll(): List<Transaction>
    suspend fun findById(idTransaction: Long): Transaction?
    suspend fun findByReservation(idReservation: Long): Transaction?
    suspend fun save(transaction: Transaction): Transaction
    suspend fun update(idTransaction: Long, transaction: Transaction): Transaction?
    suspend fun delete(idTransaction: Long): Boolean
}
