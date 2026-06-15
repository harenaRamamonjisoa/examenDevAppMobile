package transaction.services

import transaction.models.CreateTransactionRequest
import transaction.models.Transaction
import transaction.models.UpdateTransactionRequest

interface TransactionService {
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun getTransactionById(idTransaction: Long): Result<Transaction>
    suspend fun getTransactionByReservation(idReservation: Long): Result<Transaction>
    suspend fun createTransaction(request: CreateTransactionRequest): Result<Transaction>
    suspend fun updateTransaction(idTransaction: Long, request: UpdateTransactionRequest): Result<Transaction>
    suspend fun confirmTransaction(idTransaction: Long): Result<Transaction>
    suspend fun deleteTransaction(idTransaction: Long): Result<Boolean>
}
