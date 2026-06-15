package com.tco.ticket.repository

import com.tco.ticket.models.Ticket

interface TicketRepository {
    suspend fun findAll(): List<Ticket>
    suspend fun findById(idTicket: Long): Ticket?
    suspend fun findByReservation(idReservation: Long): Ticket?
    suspend fun findByUrlCode(urlCode: String): Ticket?
    suspend fun save(ticket: Ticket): Ticket
    suspend fun update(idTicket: Long, ticket: Ticket): Ticket?
    suspend fun delete(idTicket: Long): Boolean
}
