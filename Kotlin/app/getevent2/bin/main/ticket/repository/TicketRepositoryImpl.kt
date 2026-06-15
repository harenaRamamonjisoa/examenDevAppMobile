package com.tco.ticket.repository

import com.tco.mapper.toTicket
import com.tco.ticket.models.Ticket
import com.tco.ticket.models.table.TicketTable
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

class TicketRepositoryImpl(private val database: R2dbcDatabase) : TicketRepository {

    override suspend fun findAll(): List<Ticket> {
        return suspendTransaction(database) {
            TicketTable
                .selectAll()
                .map { row -> row.toTicket() }
                .toList()
        }
    }

    override suspend fun findById(idTicket: Long): Ticket? {
        return suspendTransaction(database) {
            TicketTable
                .selectAll()
                .where { TicketTable.idTicket eq idTicket }
                .map { row -> row.toTicket() }
                .singleOrNull()
        }
    }

    override suspend fun findByReservation(idReservation: Long): Ticket? {
        return suspendTransaction(database) {
            TicketTable
                .selectAll()
                .where { TicketTable.idReservation eq idReservation }
                .map { row -> row.toTicket() }
                .singleOrNull()
        }
    }

    override suspend fun findByUrlCode(urlCode: String): Ticket? {
        return suspendTransaction(database) {
            TicketTable
                .selectAll()
                .where { TicketTable.urlCode eq urlCode }
                .map { row -> row.toTicket() }
                .singleOrNull()
        }
    }

    override suspend fun save(ticket: Ticket): Ticket {
        return suspendTransaction(database) {
            val resultRows = TicketTable.insertReturning(listOf(TicketTable.idTicket)) {
                it[urlCode] = ticket.urlCode
                it[dateGeneration] = ticket.dateGeneration
                it[estUtilise] = ticket.estUtilise
                it[idReservation] = ticket.idReservation
            }
            val generatedId = resultRows.first()[TicketTable.idTicket]
            ticket.copy(idTicket = generatedId)
        }
    }

    override suspend fun update(idTicket: Long, ticket: Ticket): Ticket? {
        return suspendTransaction(database) {
            val affectedRows = TicketTable.update({ TicketTable.idTicket eq idTicket }) { row ->
                row[urlCode] = ticket.urlCode
                row[dateGeneration] = ticket.dateGeneration
                row[estUtilise] = ticket.estUtilise
                row[idReservation] = ticket.idReservation
            }
            if (affectedRows > 0) ticket.copy(idTicket = idTicket) else null
        }
    }

    override suspend fun delete(idTicket: Long): Boolean {
        return suspendTransaction(database) {
            val affectedRows = TicketTable.deleteWhere { TicketTable.idTicket eq idTicket }
            affectedRows > 0
        }
    }
}
