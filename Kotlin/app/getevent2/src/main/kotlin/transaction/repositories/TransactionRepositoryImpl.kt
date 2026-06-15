package transaction.repositories

import com.tco.mapper.toTransaction
import com.tco.transaction.models.table.TransactionTable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import transaction.models.Transaction

class TransactionRepositoryImpl(private val database: R2dbcDatabase) : TransactionRepository {

    override suspend fun findAll(): List<Transaction> {
        return suspendTransaction(database) {
            TransactionTable
                .selectAll()
                .map { row -> row.toTransaction() }
                .toList()
        }
    }

    override suspend fun findById(idTransaction: Long): Transaction? {
        return suspendTransaction(database) {
            TransactionTable
                .selectAll()
                .where { TransactionTable.idTransaction eq idTransaction }
                .map { row -> row.toTransaction() }
                .singleOrNull()
        }
    }

    override suspend fun findByReservation(idReservation: Long): Transaction? {
        return suspendTransaction(database) {
            TransactionTable
                .selectAll()
                .where { TransactionTable.idReservation eq idReservation }
                .map { row -> row.toTransaction() }
                .singleOrNull()
        }
    }

    override suspend fun save(transaction: Transaction): Transaction {
        return suspendTransaction(database) {
            val resultRows = TransactionTable.insertReturning(listOf(TransactionTable.idTransaction)) {
                it[montant] = transaction.montant
                it[dateTransaction] = transaction.dateTransaction
                it[modePaiement] = transaction.modePaiement
                it[statutPaiement] = transaction.statutPaiement.toString()
                it[referencePaiement] = transaction.referencePaiement
                it[idReservation] = transaction.idReservation
            }
            val generatedId = resultRows.first()[TransactionTable.idTransaction]
            transaction.copy(idTransaction = generatedId)
        }
    }

    override suspend fun update(idTransaction: Long, transaction: Transaction): Transaction? {
        return suspendTransaction(database) {
            val affectedRows = TransactionTable.update({ TransactionTable.idTransaction eq idTransaction }) { row ->
                row[montant] = transaction.montant
                row[dateTransaction] = transaction.dateTransaction
                row[modePaiement] = transaction.modePaiement
                row[statutPaiement] = transaction.statutPaiement.toString()
                row[referencePaiement] = transaction.referencePaiement
                row[idReservation] = transaction.idReservation
            }
            if (affectedRows > 0) transaction.copy(idTransaction = idTransaction) else null
        }
    }

    override suspend fun delete(idTransaction: Long): Boolean {
        return suspendTransaction(database) {
            val affectedRows = TransactionTable.deleteWhere { TransactionTable.idTransaction eq idTransaction }
            affectedRows > 0
        }
    }
}
